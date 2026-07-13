package org.example.service.payment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.database.*;
import org.example.exception.NotFoundException;
import org.example.exception.PaymentException;
import org.example.exception.SumLessThenMin;
import org.example.model.*;
import org.example.model.Basket;
import org.example.model.BasketDetail;
import org.example.model.CreateOrderRequest;
import org.example.model.CreateOrderResponse;
import org.example.model.PaymentCardRequest;
import org.example.model.PaymentCardResponse;
import org.example.model.PaymentRequest;
import org.example.model.RefundResponse;
import org.example.model.paymentCardGateway.Card;
import org.example.model.paymentCardGateway.PanBlock;
import org.example.model.paymentCardGateway.PaymentCardGatewayRequest;
import org.example.model.paymentCardGateway.TokenGetaway;
import org.example.model.paymentMethodRequest.CVV2Block;
import org.example.model.paymentMethodRequest.PaymentMethodRequest;
import org.example.model.paymentMethodRequest.AuthenticationData;
import org.example.model.paymentMethodRequest.TranData;
import org.example.service.account.AccountApiInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.example.exception.IllegalArgumentException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentApiService implements PaymentApiInterface {

    private final UserRepository userRepository;

    private final PaymentRepository paymentRepository;

    private final BasketRepository basketRepository;

    private final OrderRepository orderRepository;

    private final BasketDetailRepository basketDetailRepository;

    private final AccountApiInterface accountApiInterface;

    @Value("${payment.service.create-order-url}")
    private String createOrderPath;

    @Value("${payment.service.set-src-token-url}")
    private String paymentCardServiceUrl;

    @Value("${payment.service.exec-tran-url}")
    private String executeTransactionUrl;

    @Value("${payment.order.type-rid}")
    private String orderTypeRid;

    @Value("${payment.basic.auth}")
    private String authentication;

    private static final String LANGUAGE = "en";

    private static final String CURRENCY = "USD";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public CreateOrderResponse createPayment(CreateOrderRequest createOrderRequest) {
        String email = authenticationUser();
        UserEntity user = existUserEntity(email);

        Basket basket = accountApiInterface.getBasket();
        validationBasket(basket);

        PaymentGatewayRequest gatewayRequest = buildGatewayRequest(basket.getTotalPrice());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String auth = Base64.getEncoder()
                    .encodeToString(authentication.getBytes());

            headers.set("Authorization", "Basic " + auth);

            HttpEntity<PaymentGatewayRequest> entity = new HttpEntity<>(gatewayRequest, headers);
            ResponseEntity<OrderResponse> response = restTemplate.exchange(
                    createOrderPath,
                            HttpMethod.POST,
                            entity,
                            OrderResponse.class
                    );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new PaymentException("Payment service error");
            }

            OrderResponse gatewayResponse = response.getBody();
            if (gatewayResponse == null) {
                throw new PaymentException("Empty response from payment service");
            }

            OrderDetails orderDetails = gatewayResponse.getOrder();
            if (orderDetails == null) {
             throw new PaymentException("Invalid response: missing order details");
            }

            String orderStatusFromTxpg = orderDetails.getStatus();

            BigDecimal sum = sumCalculatorBasketDetail(basket);

            PaymentEntity payment = createPaymentEntity(sum, orderStatusFromTxpg, gatewayResponse);
            OrderEntity order = createOrderRequest(user, payment, gatewayResponse);

            payment.setOrder(order);

            orderRepository.save(order);
            paymentRepository.save(payment);

            return new
                    CreateOrderResponse(
                    true,
                    gatewayResponse,
                    "Payment created successfully"
            );

        } catch (RestClientException e) {
            throw new PaymentException("Payment service unavailable");
        }
    }

    @Override
    public PaymentCardResponse saveCard(PaymentCardRequest paymentCardRequest) {
        String email = authenticationUser();
        UserEntity user = existUserEntity(email);

        Basket basket = accountApiInterface.getBasket();
        validationBasket(basket);

        OrderEntity order = existOrder(user.getId());
        PaymentEntity payment = existPayment(order.getId());

        payment.setIsCardSaved(true);

        paymentRepository.save(payment);

        PaymentCardGatewayRequest paymentCardGatewayRequest = buildCardGatewayRequest(paymentCardRequest);

        String url = paymentCardServiceUrl.replace("{id}", String.valueOf(payment.getPaymentOrderId())) + "?password=" + order.getPassword();

        try {
            ResponseEntity<PaymentCardResponse> response = restTemplate.postForEntity(
                            url,
                            paymentCardGatewayRequest,
                            PaymentCardResponse.class
                    );
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new PaymentException("Payment service error");
            }

            PaymentCardResponse gatewayResponse = response.getBody();

            if (gatewayResponse == null) {
                throw new PaymentException("Empty response from payment service");
            }

            gatewayResponse.setSuccess(true);
            gatewayResponse.setMessage("Card details added");

            return gatewayResponse;

        } catch (RestClientException e) {
            throw new PaymentException("Payment service unavailable");
        }
    }

    @Override
    public PaymentResponse payment(PaymentRequest paymentRequest) {
        String email = authenticationUser();
        UserEntity user = existUserEntity(email);

        Basket basket = accountApiInterface.getBasket();
        validationBasket(basket);

        OrderEntity order = existOrder(user.getId());
        PaymentEntity payment = existPayment(order.getId());

        validationPayment(payment);

        PaymentMethodRequest paymentMethodRequest = buildPayGatewayRequest(paymentRequest, payment);

        String url = executeTransactionUrl.replace("{id}", String.valueOf(payment.getPaymentOrderId())) + "?password=" + order.getPassword();

        try {
            ResponseEntity<PaymentResponse> response = restTemplate.postForEntity(
                           url,
                            paymentMethodRequest,
                            PaymentResponse.class
                    );
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new PaymentException("Payment service error");
            }

            PaymentResponse paymentResponse = response.getBody();
            if (paymentResponse == null) {
                throw new PaymentException("Empty response from payment service");
            }

            payment.setUpdatedAt(LocalDateTime.now());

            paymentRepository.save(payment);

            clearBasketAfterPayment();

            return paymentResponse;

        } catch (RestClientException e) {
            payment.setUpdatedAt(LocalDateTime.now());

            paymentRepository.save(payment);

            throw new PaymentException("Payment service unavailable");
        }
    }

    @Override
    public RefundResponse refundPayBooksById(Integer paymentId) {
        String email = authenticationUser();
        UserEntity user = existUserEntity(email);

        Basket basket = accountApiInterface.getBasket();
        validationBasket(basket);

        OrderEntity order = existOrder(user.getId());
        PaymentEntity payment = existPaymentByPaymentOrderId(paymentId);

        if (payment == null) throw new NotFoundException("Payment not found for order: " + paymentId);

        BigDecimal totalRefundAmount = BigDecimal.ZERO;
        totalRefundAmount = totalRefundAmount.add(payment.getSumOfPay());

        PaymentMethodRequest paymentMethodRequest = buildRefundPayGatewayRequest(totalRefundAmount);

        String url = executeTransactionUrl.replace("{id}", String.valueOf(payment.getPaymentOrderId())) + "?password=" + order.getPassword();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String auth = Base64.getEncoder()
                    .encodeToString(authentication.getBytes());

            headers.set("Authorization", "Basic " + auth);

            HttpEntity<PaymentMethodRequest> entity = new HttpEntity<>(paymentMethodRequest, headers);

            ResponseEntity<RefundResponse> response = restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            RefundResponse.class
                    );
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new PaymentException("Payment service error");
            }

            RefundResponse refundResponse = response.getBody();
            if (refundResponse == null) {
                throw new PaymentException("Empty response from payment service");
            }

            payment.setUpdatedAt(LocalDateTime.now());
            refundResponse = changeRefundResponse(refundResponse, totalRefundAmount, payment.getPaymentOrderId());

            paymentRepository.save(payment);

            return refundResponse;

        } catch (RestClientException e) {
            throw new PaymentException("Payment service unavailable");
        }
    }

    private String authenticationUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private UserEntity existUserEntity(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private OrderEntity existOrder(Integer userId) {
        return orderRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException("Order not found"));
    }

    private PaymentEntity existPayment(Integer orderId) {
        return paymentRepository.findByOrderId(orderId).orElseThrow(() -> new NotFoundException("Payment not found"));
    }

    private PaymentEntity existPaymentByPaymentOrderId(Integer paymentId) {
        return paymentRepository.findByPaymentOrderId(paymentId).orElseThrow(() -> new NotFoundException("Payment not found"));
    }

    private void validationPayment(PaymentEntity payment) {
        if (!payment.getIsCardSaved()) {
            throw new PaymentException("Card details aren't exist");
        }
    }

    private void validationBasket(Basket basket) {
        if (basket == null) throw new NotFoundException("Basket does not exist");

        BigDecimal totalPrice = basket.getTotalPrice();
        if (totalPrice == null) throw new IllegalArgumentException("Total price can't be null");
        if (totalPrice.compareTo(BigDecimal.ZERO) < 0) throw new SumLessThenMin("Total price can't be less then 1.00 rub");
    }

    private PaymentEntity createPaymentEntity(BigDecimal sum, String orderStatusFromTxpg, OrderResponse gatewayResponse) {
        PaymentEntity payment = new PaymentEntity();
        payment.setPaymentOrderId(gatewayResponse.getOrder().getId());
        payment.setSumOfPay(sum);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        payment.setStatus(orderStatusFromTxpg);

        return paymentRepository.save(payment);
    }

    private OrderEntity createOrderRequest(UserEntity user, PaymentEntity payment, OrderResponse gatewayResponse) {
        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setRecipientName(user.getSurname());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setPayment(payment);
        order.setPassword(gatewayResponse.getOrder().getPassword());

        return order;
    }

    private RefundResponse changeRefundResponse(RefundResponse refundResponse, BigDecimal totalRefundAmount, Integer paymentOrderId) {
        refundResponse.setRefundAmount(totalRefundAmount.doubleValue());
        refundResponse.setOrderId(paymentOrderId);
        refundResponse.setReason("I don't like this book anymore");
        return refundResponse;
    }

    private PaymentGatewayRequest buildGatewayRequest(BigDecimal totalPrice) {
        OrderGateway order = new OrderGateway();
        order.setTypeRid(orderTypeRid);
        order.setCurrency(CURRENCY);
        order.setLanguage(LANGUAGE);

        order.setAmount(totalPrice);

        PaymentGatewayRequest gatewayRequest = new PaymentGatewayRequest();
        gatewayRequest.setOrder(order);
        return gatewayRequest;
    }

    private PaymentCardGatewayRequest buildCardGatewayRequest(PaymentCardRequest paymentCardRequest) {
        TokenGetaway tokenGetaway = new TokenGetaway();
        Card card = new Card();
        PanBlock panBlock = new PanBlock();

        panBlock.setData(paymentCardRequest.getPAN());
        card.setPanBlock(panBlock);
        card.setExpiration(paymentCardRequest.getExpiration());
        tokenGetaway.setCard(card);

        PaymentCardGatewayRequest paymentCardGatewayRequest = new PaymentCardGatewayRequest();
        paymentCardGatewayRequest.setToken(tokenGetaway);

        return paymentCardGatewayRequest;
    }

    private PaymentMethodRequest buildPayGatewayRequest(PaymentRequest paymentRequest, PaymentEntity paymentEntity) {
        String phase = "Single";

        TranData tranData = new TranData();
        AuthenticationData authenticationData = new AuthenticationData();
        CVV2Block cvv2Block = new CVV2Block();

        if (cvv2Block.getData() != null && !paymentRequest.getData().isBlank()) {
            cvv2Block.setData(paymentRequest.getData());
            authenticationData.setCvv2Block(cvv2Block);
            tranData.setAuthentication(authenticationData);
        }

        tranData.setPhase(phase);
        tranData.setAmount(paymentEntity.getSumOfPay().toString());

        PaymentMethodRequest paymentMethodRequest = new PaymentMethodRequest();
        paymentMethodRequest.setTran(tranData);

        return paymentMethodRequest;
    }

    private PaymentMethodRequest buildRefundPayGatewayRequest(BigDecimal totalAmount) {
        String phase = "Single";
        String type = "Refund";

        TranData tranData = new TranData();
        tranData.setPhase(phase);
        tranData.setAmount(totalAmount.toString());
        tranData.setType(type);

        PaymentMethodRequest paymentMethodRequest = new PaymentMethodRequest();
        paymentMethodRequest.setTran(tranData);

        return paymentMethodRequest;
    }

    private void clearBasketAfterPayment() {
        String email = authenticationUser();
        UserEntity user = existUserEntity(email);

        BasketEntity basket = user.getBasket();
        if (basket == null) {
            return;
        }

        List<BasketDetailEntity> basketDetails = basket.getBasketDetails();
        if (basketDetails == null || basketDetails.isEmpty()) {
            return;
        }
        basketDetailRepository.deleteAll(basketDetails);
        basket.getBasketDetails().clear();

        basket.setTotalPrice(BigDecimal.ZERO);
        basket.setQuantityBooks(0);
        basket.setUpdatedAt(LocalDateTime.now());

        basketRepository.save(basket);
    }

    private BigDecimal sumCalculatorBasketDetail(Basket basket) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BasketDetail basketDetail : basket.getBasketDetails()) {
            sum = basketDetail.getPrice().add(sum);
        }
        return sum;
    }
}
