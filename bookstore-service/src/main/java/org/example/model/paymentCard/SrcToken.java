package org.example.model.paymentCard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SrcToken {
    private String id;
    private String paymentMethod;
    private String role;
    private String status;
    private String regTime;
    private String displayName;
    private CardInfo card;

}
