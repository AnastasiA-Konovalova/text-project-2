package org.example.model;

import lombok.Getter;
import lombok.Setter;
import org.example.model.paymentMethodResponse.Tran;

@Getter
@Setter
public class PaymentResponse {
    private Tran tran;
}
