package dto.paymentCard;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SaveCardResponse {
    private String status;
    private DCC dcc;
    private Surcharge surcharge;
    private String cvv2AuthStatus;
    private String otpAutStatus;
    private SrcToken srcToken;
}

