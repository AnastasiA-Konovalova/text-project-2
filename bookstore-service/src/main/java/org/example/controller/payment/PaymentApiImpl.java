package org.example.controller.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.api.PaymentApi;
import org.example.model.*;
import org.example.model.CreateOrderRequest;
import org.example.model.CreateOrderResponse;
import org.example.model.PaymentCardRequest;
import org.example.model.PaymentCardResponse;
import org.example.model.PaymentRequest;
import org.example.model.RefundResponse;
import org.example.model.PaymentResponse;
import org.example.service.payment.PaymentApiInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class PaymentApiImpl implements PaymentApi {

    private final PaymentApiInterface paymentApiInterface;

    @Override
    public ResponseEntity<PaymentCardResponse> saveCard(@RequestBody @Valid PaymentCardRequest paymentCardRequest) {
        return ResponseEntity.ok(paymentApiInterface.saveCard(paymentCardRequest));
    }

    @Override
    public ResponseEntity<CreateOrderResponse> createPayment(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        return ResponseEntity.ok(paymentApiInterface.createPayment(createOrderRequest));
    }

    @Override
    public ResponseEntity<PaymentResponse> payment(Integer orderId, @RequestBody @Valid PaymentRequest paymentRequest) {
        return ResponseEntity.ok(paymentApiInterface.payment(orderId, paymentRequest));
    }

    @Override
    public ResponseEntity<RefundResponse> refundPayBooksById(Integer orderId) {
        return ResponseEntity.ok(paymentApiInterface.refundPayBooksById(orderId));
    }
}