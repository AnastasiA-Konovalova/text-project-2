package org.example.payment;

import org.example.api.PaymentApi;
import org.example.model.PaymentRequest;
import org.example.model.PaymentResponse;
import org.example.model.RefundPaymentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentApiImpl implements PaymentApi {

    @Override
    public ResponseEntity<PaymentResponse> postPaymentPay(PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse = new PaymentResponse();
        return ResponseEntity.ok(paymentResponse);
    }

    @Override
    public ResponseEntity<PaymentResponse> refundPayBooksById(RefundPaymentRequest refundPaymentRequest) {
        PaymentResponse paymentResponse = new PaymentResponse();
        return ResponseEntity.ok(paymentResponse);
    }
}
