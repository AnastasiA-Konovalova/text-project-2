package org.example.service.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.database.BasketDetailRepository;
import org.example.database.BasketRepository;
import org.example.database.PaymentRepository;
import org.example.database.UserRepository;
import org.example.exception.NotFoundException;
import org.example.exception.PaymentException;
import org.example.exception.SumLessThenMin;
import org.example.model.*;
import org.example.model.BasketDetail;
import org.example.model.CreateOrderRequest;
import org.example.model.CreateOrderResponse;
import org.example.model.PaymentCardRequest;
import org.example.model.PaymentCardResponse;
import org.example.model.PaymentRequest;
import org.example.model.RefundPaymentRequest;
import org.example.model.RefundResponse;
import org.example.model.paymentCardGateway.Card;
import org.example.model.paymentCardGateway.PanBlock;
import org.example.model.paymentCardGateway.PaymentCardGatewayRequest;
import org.example.model.paymentCardGateway.TokenGetaway;
import org.example.model.paymentMethodRequest.CVV2Block;
import org.example.model.paymentMethodRequest.PaymentMethodRequest;
import org.example.model.paymentMethodRequest.AuthenticationData;
import org.example.model.PaymentResponse;
import org.example.model.paymentMethodRequest.TranData;
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
public class PaymentApiService implements PaymentApiInterface {

    private final UserRepository userRepository;

    private final PaymentRepository paymentRepository;

    private final BasketRepository basketRepository;

    private final BasketDetailRepository basketDetailRepository;

    @Value("${payment.service.url:http://localhost:8004/order}")
    private String paymentServiceUrl;

    @Value("${payment.service.url:http://localhost:8004/order/{id}/set-src-token}")
    private String paymentCardServiceUrl;

    @Value("${payment.service.url:http://localhost:8004/order/{id}/exec-tran}")
    private String executeTransactionUrl;

    @Value("${payment.order.type-rid:AK_Sale_Order_Base}")
    private String orderTypeRid;

    private Integer id;

    private String orderPassword;

    private boolean status = false;

    private static final String LANGUAGE = "en";

    private static final String CURRENCY = "USD";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest) {

        validation(createOrderRequest);

        PaymentEntity payment = createPaymentEntity(createOrderRequest);

        PaymentGatewayRequest gatewayRequest = buildGatewayRequest(createOrderRequest);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String auth = Base64.getEncoder()
                    .encodeToString("TerminalSys/A.Konovalova@compassplus.com:1234".getBytes());

            headers.set("Authorization", "Basic " + auth);

            HttpEntity<PaymentGatewayRequest> entity = new HttpEntity<>(gatewayRequest, headers);
            ResponseEntity<OrderResponse> response =
                    restTemplate.exchange(
                            paymentServiceUrl,
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

            payment.setOrderId(gatewayResponse.getOrder().getId());

            BigDecimal sum = BigDecimal.ZERO;
            for (BasketDetail basketDetail : createOrderRequest.getBasket().getBasketDetails()) {
                sum = basketDetail.getPrice().add(sum);
            }

            payment.setSumOfPay(sum);
            payment.setCreatedAt(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());
            payment.setStatus(PaymentResponse.StatusEnum.PREPARING.getValue());

            id = gatewayResponse.getOrder().getId();
            orderPassword = gatewayResponse.getOrder().getPassword();

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
    public PaymentCardResponse addPaymentDetails(PaymentCardRequest paymentCardRequest) {
        validationCard(paymentCardRequest);

        PaymentCardGatewayRequest paymentCardGatewayRequest = buildCardGatewayRequest(paymentCardRequest);
        String url = paymentCardServiceUrl.replace("{id}", String.valueOf(id)) + "?password=" + orderPassword;

        try {
            ResponseEntity<PaymentCardResponse> response =
                    restTemplate.postForEntity(
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

            status = true;
            return gatewayResponse;

        } catch (RestClientException e) {
            throw new PaymentException("Payment service unavailable");
        }
    }

    @Override
    public PaymentResponse payment(PaymentRequest paymentRequest) {
        validationPayment(paymentRequest);

        if(paymentRequest.getOrderId() == null) {
            throw new PaymentException("Order ID is required fir payment");
        }
        PaymentEntity paymentEntity = paymentRepository.findByOrderId(id);

        PaymentMethodRequest paymentMethodRequest = buildPayGatewayRequest(paymentRequest, paymentEntity);

        String url = executeTransactionUrl.replace("{id}", String.valueOf(id)) + "?password=" + orderPassword;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String auth = Base64.getEncoder()
                    .encodeToString("TerminalSys/A.Konovalova@compassplus.com:1234".getBytes());

            headers.set("Authorization", "Basic " + auth);

            HttpEntity<PaymentMethodRequest> entity = new HttpEntity<>(paymentMethodRequest, headers);

            ResponseEntity<PaymentResponse> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            PaymentResponse.class
                    );
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new PaymentException("Payment service error");
            }

            PaymentResponse paymentResponse = response.getBody();

            if (paymentResponse == null) {
                throw new PaymentException("Empty response from payment service");
            }

            paymentResponse.setPaymentMethod(PaymentResponse.PaymentMethodEnum.CARD);
            paymentResponse.setStatus(PaymentResponse.StatusEnum.FULLY_PAID);
            paymentResponse.setOrderId(paymentEntity.getOrderId());

            paymentEntity.setStatus(PaymentResponse.StatusEnum.FULLY_PAID.getValue());
            paymentEntity.setUpdatedAt(LocalDateTime.now());

            paymentRepository.save(paymentEntity);

            clearBasketAfterPayment();

            return paymentResponse;

        } catch (RestClientException e) {
            paymentEntity.setStatus(PaymentResponse.StatusEnum.DECLINED.getValue());
            paymentEntity.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(paymentEntity);

            throw new PaymentException("Payment service unavailable");
        }
    }

    @Override
    public RefundResponse refundPayBooksById(Integer paymentId, RefundPaymentRequest refundPaymentRequest) {
        PaymentEntity paymentEntity = paymentRepository.findByOrderId(paymentId);
        if (paymentEntity == null) throw new NotFoundException("Payment not found for order: " + paymentId);

        PaymentResponse.StatusEnum currentStatus;
        try {
            currentStatus = PaymentResponse.StatusEnum.fromValue(paymentEntity.getStatus());
        } catch (IllegalArgumentException e) {
            throw new PaymentException("Invalid payment status " + paymentEntity.getStatus());
        }
        validationStatus(currentStatus, paymentEntity);
        validationRefundPaymentRequest(refundPaymentRequest);

        BigDecimal totalRefundAmount = BigDecimal.ZERO;
        totalRefundAmount = totalRefundAmount.add(paymentEntity.getSumOfPay());

        validationRefund(paymentEntity, refundPaymentRequest);
        PaymentMethodRequest paymentMethodRequest = buildRefundPayGatewayRequest(refundPaymentRequest, totalRefundAmount);

        String url = executeTransactionUrl.replace("{id}", String.valueOf(id)) + "?password=" + orderPassword;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String auth = Base64.getEncoder()
                    .encodeToString("TerminalSys/A.Konovalova@compassplus.com:1234".getBytes());

            headers.set("Authorization", "Basic " + auth);

            HttpEntity<PaymentMethodRequest> entity = new HttpEntity<>(paymentMethodRequest, headers);

            ResponseEntity<RefundResponse> response =
                    restTemplate.exchange(
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

            paymentEntity.setStatus(PaymentResponse.StatusEnum.REFUNDED.getValue());
            paymentEntity.setUpdatedAt(LocalDateTime.now());
            refundResponse.setRefundAmount(totalRefundAmount.doubleValue());
            refundResponse.setOrderId(id);
            refundResponse.setReason("I don't like this book anymore");

            paymentRepository.save(paymentEntity);

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

    private void validationCard(PaymentCardRequest paymentCardRequest) {
        String email = authenticationUser();
        existUserEntity(email);
        if (paymentCardRequest == null) throw new IllegalArgumentException("Card details are empty.");

        if (paymentCardRequest.getPAN() == null) throw new IllegalArgumentException("PAN can't be empty");
        int PAN = paymentCardRequest.getPAN().length();
        if (PAN < 16) throw new IllegalArgumentException("PAN length can't be less then 16");

        if (paymentCardRequest.getCVV() == null) throw new IllegalArgumentException("CVV can't be empty");
        int CVV = paymentCardRequest.getCVV().length();
        if (CVV != 3) throw new IllegalArgumentException("CVV length should be three");

        if (paymentCardRequest.getExpiration() == null) throw new IllegalArgumentException("Expiration can't be empty");
        int expiration = paymentCardRequest.getExpiration().length();
        if (expiration != 4) throw new IllegalArgumentException("Expiration length should be four");
    }

    private void validationPayment(PaymentRequest paymentRequest) {
        String email = authenticationUser();
        existUserEntity(email);

        if (!status) {
            throw new PaymentException("Card details aren't exist");
        }
        if (paymentRequest.getPaymentMethod() != PaymentRequest.PaymentMethodEnum.CARD) {
            throw new PaymentException("Payment method should be 'CARD'");
        }
    }

    private void validationRefund(PaymentEntity paymentEntity, RefundPaymentRequest refundPaymentRequest) {
        String email = authenticationUser();
        existUserEntity(email);
        if (!paymentEntity.getStatus().equals(PaymentResponse.StatusEnum.FULLY_PAID.getValue())) {
            throw new PaymentException("For refund status should be 'fullyPaid'");
        }
        if (refundPaymentRequest.getOrderIds() == null) throw new PaymentException("Order ids are empty.");
    }

    private void validation(CreateOrderRequest createOrderRequest) {
        String email = authenticationUser();
        existUserEntity(email);
        if (createOrderRequest.getBasket() == null) throw new NotFoundException("Basket does not exist");

        BigDecimal totalPrice = createOrderRequest.getBasket().getTotalPrice();
        if (totalPrice == null) throw new IllegalArgumentException("Total price can't be null");
        if (createOrderRequest.getBasket().getTotalPrice().compareTo(BigDecimal.ZERO) < 0)
            throw new SumLessThenMin("Total price can't be less then 1.00 rub");
    }

    private PaymentEntity createPaymentEntity(CreateOrderRequest createOrderRequest) {
        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setPaymentMethod(
                PaymentRequest.PaymentMethodEnum.valueOf(createOrderRequest.getPaymentMethod().getValue())
        );
        paymentEntity.setSumOfPay(createOrderRequest.getBasket().getTotalPrice());
        paymentEntity.setCreatedAt(LocalDateTime.now());
        paymentEntity.setUpdatedAt(LocalDateTime.now());

        return paymentRepository.save(paymentEntity);
    }

    private PaymentGatewayRequest buildGatewayRequest(CreateOrderRequest createOrderRequest) {
        OrderGateway order = new OrderGateway();
        order.setTypeRid(orderTypeRid);
        order.setCurrency(CURRENCY);
        order.setLanguage(LANGUAGE);

        String totalPrice = createOrderRequest.getBasket().getTotalPrice().toString();
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
        String data = "704";

        TranData tranData = new TranData();
        AuthenticationData authenticationData = new AuthenticationData();
        CVV2Block cvv2Block = new CVV2Block();

        cvv2Block.setData(data);
        authenticationData.setCvv2Block(cvv2Block);
        tranData.setPhase(phase);
        tranData.setAmount(paymentEntity.getSumOfPay().toString());
        tranData.setAuthentication(authenticationData);

        PaymentMethodRequest paymentMethodRequest = new PaymentMethodRequest();
        paymentMethodRequest.setTran(tranData);

        return paymentMethodRequest;
    }

    private PaymentMethodRequest buildRefundPayGatewayRequest(RefundPaymentRequest refundPaymentRequest, BigDecimal totalAmount) {
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

    private void validationStatus(PaymentResponse.StatusEnum currentStatus, PaymentEntity paymentEntity) {
        if (currentStatus == null) {
            throw new PaymentException("Payment status is null");
        }

        if (currentStatus != PaymentResponse.StatusEnum.FULLY_PAID) {
            throw new PaymentException(
                    "Cannot refund payment with status: " + currentStatus.getValue() +
                            ". Only FULLY_PAID payments can be refunded."
            );
        }

        if (currentStatus == PaymentResponse.StatusEnum.REFUNDED) {
            throw new PaymentException("Payment has already refunded");
        }
    }
    private void validationRefundPaymentRequest(RefundPaymentRequest refundPaymentRequest) {
        if (refundPaymentRequest == null ||
                refundPaymentRequest.getOrderIds() == null ||
                refundPaymentRequest.getOrderIds().isEmpty()) {
            throw new IllegalArgumentException("Order IDs list cannot be empty");
        }
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
}
