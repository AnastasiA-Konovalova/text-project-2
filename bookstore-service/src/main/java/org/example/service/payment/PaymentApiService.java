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
import org.example.model.CreateOrderResponse;
import org.example.model.CreateOrderRequest;
import org.example.model.PaymentCardRequest;
import org.example.model.PaymentCardResponse;
import org.example.model.PaymentRequest;
import org.example.model.PaymentResponse;
import org.example.model.PaymentStatus;
import org.example.model.RefundResponse;
import dto.paymentCard.SaveCardResponse;
import dto.paymentCardGateway.Card;
import dto.paymentCardGateway.PanBlock;
import dto.paymentCardGateway.PaymentCardGatewayRequest;
import dto.paymentCardGateway.TokenGetaway;
import dto.paymentMethodRequest.CVV2Block;
import dto.paymentMethodRequest.PaymentMethodRequest;
import dto.paymentMethodRequest.AuthenticationData;
import dto.paymentMethodRequest.TranData;
import dto.paymentMethodResponse.PaymentExecutionResponse;
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
import java.time.OffsetDateTime;
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

    public static final String PHASE_SINGLE = "Single";

    public static final String TXPG_TYPE_REFUND = "Refund";

    public static final String AUTH_HEADER_BASIC = "Basic ";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public CreateOrderResponse createPayment(CreateOrderRequest createOrderRequest) {
        UserEntity user = getAuthenticatedUser();

        Basket basket = accountApiInterface.getBasket();
        validationBasket(basket);

        BigDecimal totalPrice = basket.getTotalPrice();
        OrderResponse txpgResponse = callTxpgCreatePayment(
                buildGatewayRequest(totalPrice)
        );

        String orderStatusFromTxpg = txpgResponse.getOrder().getStatus();

        PaymentEntity payment = createPaymentEntity(totalPrice, orderStatusFromTxpg, txpgResponse);
        OrderEntity order = createOrderEntity(user, payment, createOrderRequest);

        payment.setOrder(order);
        paymentRepository.save(payment);

        clearBasketAfterPayment();

        return createOrderResponse(order);
    }

    @Override
    public PaymentCardResponse saveCard(Integer orderId, PaymentCardRequest paymentCardRequest) {
        UserEntity user = getAuthenticatedUser();

        OrderEntity order = existOrderByOrderId(orderId);
        if (!order.getUser().getId().equals(user.getId())) {
            throw new PaymentException("Order does not belong to the current user");
        }

        PaymentEntity payment = order.getPayment();
        if (payment == null) {
            throw new PaymentException("Payment not found for order: " + orderId);
        }
        callTxpgSaveCard(
                buildTxpgUrl(paymentCardServiceUrl, payment),
                buildCardGatewayRequest(paymentCardRequest)
        );

        payment.setUpdatedAt(LocalDateTime.now());

        order.setPayerEmail(paymentCardRequest.getEmail());
        order.setPayerName(paymentCardRequest.getName());
        order.setStatus(OrderStatus.CARD_SAVED);

        paymentRepository.save(payment);

        return createPaymentCardResponse(user.getEmail(), user.getSurname(), order);
    }

    @Override
    public PaymentResponse payment(Integer orderId, PaymentRequest paymentRequest) {
        UserEntity user = getAuthenticatedUser();

        OrderEntity order = existOrderByOrderId(orderId);
        if (!order.getUser().getId().equals(user.getId())) {
            throw new PaymentException("Order does not belong to the current user");
        }

        PaymentEntity payment = order.getPayment();
        if (payment == null) {
            throw new PaymentException("Payment not found for order: " + orderId);
        }

        if (!OrderStatus.CARD_SAVED.equals(order.getStatus())) {
            throw new PaymentException("Status should be 'CARD_SAVED'");
        }

        PaymentExecutionResponse txpgResponse;

        try {
            txpgResponse = callTxpgPayment(
                    buildTxpgUrl(executeTransactionUrl, payment),
                    buildPayGatewayRequest(paymentRequest, payment)
            );
        } catch (RestClientException e) {
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);
            throw new PaymentException("Payment service unavailable", e);
        }

        if (!"Approved".equalsIgnoreCase(txpgResponse.getTran().getPmoResultCode())) {
            throw new PaymentException("Status should be 'approved'");
        }

        payment.setUpdatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.FULLY_PAID);
        paymentRepository.save(payment);

        return createPaymentResponse(order);
    }

    @Override
    public RefundResponse refundPayBooksById(Integer orderId) {
        UserEntity user = getAuthenticatedUser();

        OrderEntity order = existOrderByOrderId(orderId);
        if (!order.getUser().getId().equals(user.getId())) {
            throw new PaymentException("Order does not belong to the current user");
        }

        PaymentEntity payment = order.getPayment();
        if (payment == null) {
            throw new PaymentException("Payment not found for order: " + orderId);
        }

        if (OrderStatus.REFUNDED.equals(order.getStatus())) {
            throw new PaymentException("A return cannot be processed more than once");
        }

        if (!OrderStatus.FULLY_PAID.equals(order.getStatus())) {
            throw new PaymentException("Only fully paid orders can be refunded");
        }

        PaymentExecutionResponse txpgResponse;
        try {
            txpgResponse = callTxpgRefund(
                    buildTxpgUrl(executeTransactionUrl, payment),
                    buildRefundPayGatewayRequest(payment.getSumOfPay()));
        } catch (RestClientException e) {
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);
            throw new PaymentException("Refund service unavailable", e);
        }

        if (!"Approved".equalsIgnoreCase(txpgResponse.getTran().getPmoResultCode())) {
            throw new PaymentException("Status should be 'approved'");
        }

        order.setStatus(OrderStatus.REFUNDED);

        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        return createRefundResponse(order);
    }

    private String authenticationUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private UserEntity existUserEntity(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private OrderEntity existOrderByOrderId(Integer orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));
    }

    private void validationBasket(Basket basket) {
        if (basket == null) throw new NotFoundException("Basket does not exist");
        if (basket.getBasketDetails() == null || basket.getBasketDetails().isEmpty())
            throw new PaymentException("Basket has no items");
        BigDecimal totalPrice = basket.getTotalPrice();
        if (totalPrice == null) throw new IllegalArgumentException("Total price can't be null");
        if (totalPrice.compareTo(BigDecimal.ZERO) < 0)
            throw new SumLessThenMin("Total price can't be less then 1.00 rub");
    }

    private PaymentEntity createPaymentEntity(BigDecimal sum, String orderStatusFromTxpg, OrderResponse gatewayResponse) {
        PaymentEntity payment = new PaymentEntity();
        payment.setPaymentOrderId(gatewayResponse.getOrder().getId());
        payment.setSumOfPay(sum);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        payment.setStatus(orderStatusFromTxpg);
        payment.setPassword(gatewayResponse.getOrder().getPassword());

        return payment;
    }

    private OrderEntity createOrderEntity(UserEntity user, PaymentEntity payment, CreateOrderRequest createOrderRequest) {
        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setRecipientName(createOrderRequest.getRecipientName());
        order.setDeliveryAddress(createOrderRequest.getDeliveryAddress());
        order.setRecipientPhone(createOrderRequest.getRecipientPhone());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setPayment(payment);
        order.setStatus(OrderStatus.PENDING);

        return order;
    }

    private CreateOrderResponse createOrderResponse(OrderEntity order) {
        CreateOrderResponse createOrderResponse = new CreateOrderResponse();
        createOrderResponse.setAmount(order.getPayment().getSumOfPay());
        createOrderResponse.setOrderId(order.getId());
        createOrderResponse.setStatus(mapOrderStatusToPaymentStatus(order.getStatus()));
        createOrderResponse.setCreatedAt(OffsetDateTime.now());
        return createOrderResponse;
    }

    private PaymentCardResponse createPaymentCardResponse(String email, String surname, OrderEntity order) {
        PaymentCardResponse paymentCardResponse = new PaymentCardResponse();
        paymentCardResponse.setEmail(email);
        paymentCardResponse.setName(surname);
        paymentCardResponse.setStatus(mapOrderStatusToPaymentStatus(order.getStatus()));
        paymentCardResponse.setCreatedAt(OffsetDateTime.now());

        return paymentCardResponse;
    }

    private RefundResponse createRefundResponse(OrderEntity order) {
        RefundResponse refundResponse = new RefundResponse();
        refundResponse.setRefundAmount(order.getPayment().getSumOfPay());
        refundResponse.setStatus(mapOrderStatusToPaymentStatus(order.getStatus()));
        refundResponse.setOrderId(order.getId());

        return refundResponse;
    }

    private PaymentResponse createPaymentResponse(OrderEntity order) {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentMethod(PaymentResponse.PaymentMethodEnum.CARD);
        paymentResponse.setStatus(mapOrderStatusToPaymentStatus(order.getStatus()));
        paymentResponse.setOrderId(order.getId());
        paymentResponse.setCreatedAt(OffsetDateTime.now());

        return paymentResponse;
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
        TranData tranData = new TranData();
        AuthenticationData authenticationData = new AuthenticationData();
        CVV2Block cvv2Block = new CVV2Block();

        if (cvv2Block.getData() != null && !paymentRequest.getData().isBlank()) {
            cvv2Block.setData(paymentRequest.getData());
            authenticationData.setCvv2Block(cvv2Block);
            tranData.setAuthentication(authenticationData);
        }

        tranData.setPhase(PHASE_SINGLE);
        tranData.setAmount(paymentEntity.getSumOfPay().toString());

        PaymentMethodRequest paymentMethodRequest = new PaymentMethodRequest();
        paymentMethodRequest.setTran(tranData);

        return paymentMethodRequest;
    }

    private PaymentMethodRequest buildRefundPayGatewayRequest(BigDecimal totalAmount) {
        TranData tranData = new TranData();
        tranData.setPhase(PHASE_SINGLE);
        tranData.setAmount(totalAmount.toString());
        tranData.setType(TXPG_TYPE_REFUND);

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

    private OrderResponse callTxpgCreatePayment(PaymentGatewayRequest gatewayRequest) throws RestClientException {
            HttpHeaders headers = createHttpHeaders();

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
            OrderResponse body = response.getBody();

            if (body == null) {
                throw new PaymentException("Empty response from payment service");
            }

            if (body.getOrder() == null) {
                throw new PaymentException("Invalid response: missing order details");
            }

            return body;
    }

    private void callTxpgSaveCard(String url, PaymentCardGatewayRequest paymentCardGatewayRequest) throws RestClientException {
        ResponseEntity<SaveCardResponse> response = restTemplate.postForEntity(
                url,
                paymentCardGatewayRequest,
                SaveCardResponse.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new PaymentException("Payment service error");
        }

        if (response.getBody() == null) {
                throw new PaymentException("Empty response from payment service");
        }
    }

    private PaymentExecutionResponse callTxpgPayment(String url, PaymentMethodRequest paymentMethodRequest) throws RestClientException {
        ResponseEntity<PaymentExecutionResponse> response = restTemplate.postForEntity(
                url,
                paymentMethodRequest,
                PaymentExecutionResponse.class
            );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new PaymentException("Payment service error");
        }
        if (response.getBody() == null) {
            throw new PaymentException("Empty response from payment service");
        }
        return response.getBody();
    }

    private PaymentExecutionResponse callTxpgRefund(String url, PaymentMethodRequest paymentMethodRequest) throws RestClientException {
            HttpHeaders headers = createHttpHeaders();

            HttpEntity<PaymentMethodRequest> entity = new HttpEntity<>(paymentMethodRequest, headers);

            ResponseEntity<PaymentExecutionResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    PaymentExecutionResponse.class
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new PaymentException("Payment service error");
            }

            if (response.getBody() == null) {
                throw new PaymentException("Empty response from payment service");
            }

            return response.getBody();
    }

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String auth = Base64.getEncoder()
                .encodeToString(authentication.getBytes());

        headers.set("Authorization", AUTH_HEADER_BASIC + auth);

        return headers;
    }

    private PaymentStatus mapOrderStatusToPaymentStatus(OrderStatus orderStatus) {
        if (orderStatus == null) {
            return PaymentStatus.PENDING;
        }

        return switch (orderStatus) {
            case CARD_SAVED -> PaymentStatus.CARD_SAVED;
            case FULLY_PAID -> PaymentStatus.FULLY_PAID;
            case REFUNDED -> PaymentStatus.REFUNDED;
            default -> PaymentStatus.PENDING;
        };
    }

    private String buildTxpgUrl(String baseUrl, PaymentEntity payment) {
        return baseUrl.replace("{id}", String.valueOf(payment.getPaymentOrderId()))
                + "?password=" + payment.getPassword();
    }

    private UserEntity getAuthenticatedUser() {
        String email = authenticationUser();
        return existUserEntity(email);
    }
}
