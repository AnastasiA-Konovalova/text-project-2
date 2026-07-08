package org.example.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderGateway {
    private String typeRid;
    private String amount;
    private String currency;
    private String language;
}
