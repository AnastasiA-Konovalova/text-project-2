package org.example.controller.payment;

import lombok.RequiredArgsConstructor;
import org.example.api.PaymentApi;
import org.example.model.*;
import org.example.model.CreateOrderRequest;
import org.example.model.PaymentCardRequest;
import org.example.model.PaymentCardResponse;
import org.example.model.PaymentRequest;
import org.example.model.PaymentResponse;
import org.example.model.RefundPaymentRequest;
import org.example.model.RefundResponse;
import org.example.service.payment.PaymentApiInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentApiImpl implements PaymentApi {

    private final PaymentApiInterface paymentApiInterface;

    @Override
    public ResponseEntity<PaymentCardResponse> addPaymentDetails(PaymentCardRequest paymentCardRequest) {//ввести карту
        return ResponseEntity.ok(paymentApiInterface.addPaymentDetails(paymentCardRequest));
    }

    @Override
    public ResponseEntity<CreateOrderResponse> createOrder(CreateOrderRequest createOrderRequest) {
        return ResponseEntity.ok(paymentApiInterface.createOrder(createOrderRequest));
    }

    @Override
    public ResponseEntity<PaymentResponse> payment(PaymentRequest paymentRequest) {
        return ResponseEntity.ok(paymentApiInterface.payment(paymentRequest));
    }


    @Override
    public ResponseEntity<RefundResponse> refundPayBooksById(Integer paymentId, RefundPaymentRequest refundPaymentRequest) {
        return ResponseEntity.ok(paymentApiInterface.refundPayBooksById(paymentId, refundPaymentRequest));
    }
}