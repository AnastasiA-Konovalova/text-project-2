package org.example.model.paymentMethodResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tran {

    private String approvalCode;
    private Match match;
    private String pmoResultCode;
}
