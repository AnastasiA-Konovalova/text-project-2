package org.example.model.paymentCard;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderData {
    private DCC dcc;
    private Surcharge surcharge;
    private String cvv2AuthStatus;
    private String otpAutStatus;
    private SrcToken srcToken;
}

