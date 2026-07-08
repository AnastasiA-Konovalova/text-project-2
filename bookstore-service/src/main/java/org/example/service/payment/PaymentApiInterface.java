package org.example.service.payment;

import org.example.model.CreateOrderRequest;
import org.example.model.CreateOrderResponse;
import org.example.model.PaymentCardRequest;
import org.example.model.PaymentCardResponse;
import org.example.model.PaymentRequest;
import org.example.model.PaymentResponse;
import org.example.model.RefundPaymentRequest;
import org.example.model.RefundResponse;

public interface PaymentApiInterface {

    CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest);

    PaymentCardResponse addPaymentDetails(PaymentCardRequest paymentCardRequest);

    PaymentResponse payment(PaymentRequest paymentRequest);

    RefundResponse refundPayBooksById(Integer paymentId, RefundPaymentRequest refundPaymentRequest);
}
