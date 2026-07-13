package org.example.service.payment;

import org.example.model.*;
import org.example.model.CreateOrderRequest;
import org.example.model.PaymentCardRequest;
import org.example.model.PaymentRequest;
import org.example.model.RefundResponse;

public interface PaymentApiInterface {

    CreateOrderResponse createPayment(CreateOrderRequest createOrderRequest);

    PaymentCardResponse saveCard(PaymentCardRequest paymentCardRequest);

    PaymentResponse payment(PaymentRequest paymentRequest);

    RefundResponse refundPayBooksById(Integer paymentId);
}
