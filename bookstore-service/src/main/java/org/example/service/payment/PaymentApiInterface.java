package org.example.service.payment;

import org.example.model.CreateOrderRequest;
import org.example.model.CreateOrderResponse;
import org.example.model.PaymentCardRequest;
import org.example.model.PaymentCardResponse;
import org.example.model.PaymentRequest;
import org.example.model.RefundResponse;
import org.example.model.PaymentResponse;

public interface PaymentApiInterface {

    CreateOrderResponse createPayment(CreateOrderRequest createOrderRequest);

    PaymentCardResponse saveCard(Integer orderId, PaymentCardRequest paymentCardRequest);

    PaymentResponse payment(Integer orderId, PaymentRequest paymentRequest);

    RefundResponse refundPayBooksById(Integer orderId);
}
