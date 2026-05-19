package org.example.controller.payment;

import org.example.api.PaymentApi;
import org.example.model.PaymentRequest;
import org.example.model.PaymentResponse;
import org.example.model.RefundPaymentRequest;
import org.example.model.RefundResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentApiImpl implements PaymentApi {

    @Override
    public ResponseEntity<PaymentResponse> postPayment(PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse = new PaymentResponse();
        return ResponseEntity.ok(paymentResponse);    }

    @Override
    public ResponseEntity<RefundResponse> refundPayBooksById(Integer paymentId, RefundPaymentRequest refundPaymentRequest) {
        RefundResponse refundResponse = new RefundResponse();
        return ResponseEntity.ok(refundResponse);
    }
}
