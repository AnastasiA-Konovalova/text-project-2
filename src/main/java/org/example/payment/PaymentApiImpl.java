package org.example.payment;

import org.example.api.PaymentApi;
import org.example.model.PaymentRequest;
import org.example.model.PaymentResponse;
import org.example.model.RefundResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentApiImpl implements PaymentApi {

    @Override
    public ResponseEntity<RefundResponse> paymentRefundOrderIdBookIdPost(Integer orderId, Integer bookId) {
        RefundResponse refundResponse = new RefundResponse();
        return ResponseEntity.ok(refundResponse);
    }

    @Override
    public ResponseEntity<PaymentResponse> paymentPayPost(PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse = new PaymentResponse();
        return ResponseEntity.ok(paymentResponse);
    }

    @Override
    public ResponseEntity<Void> paymentBasketBookIdDelete(Integer bookId) {
        return ResponseEntity.ok().build();
    }
}
