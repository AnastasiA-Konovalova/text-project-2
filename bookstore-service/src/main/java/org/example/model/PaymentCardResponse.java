package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.model.paymentCard.OrderData;

@Getter
@Setter
@AllArgsConstructor
public class PaymentCardResponse {
    private boolean success;
    private OrderData order;
    private String message;
}

