package dto.paymentCardGateway;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Card {

    private PanBlock panBlock;

    private String expiration;
}
