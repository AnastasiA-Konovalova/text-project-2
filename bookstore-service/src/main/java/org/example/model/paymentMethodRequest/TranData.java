package org.example.model.paymentMethodRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TranData {

    private String phase;

    private String amount;

    private String type;

    private AuthenticationData authentication;
}
