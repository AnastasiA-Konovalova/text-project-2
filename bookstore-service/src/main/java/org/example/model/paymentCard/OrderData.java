package org.example.model.paymentCard;

import lombok.Getter;
import lombok.Setter;
import org.example.model.CreateOrderRequest.StatusEnum;

@Setter
@Getter
public class OrderData {
    private StatusEnum status;
    private DCC dcc;
    private Surcharge surcharge;
    private String cvv2AuthStatus;
    private String otpAutStatus;
    private SrcToken srcToken;
}

