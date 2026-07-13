package org.example.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderGateway {
    private String typeRid;
    private BigDecimal amount;
    private String currency;
    private String language;
}
