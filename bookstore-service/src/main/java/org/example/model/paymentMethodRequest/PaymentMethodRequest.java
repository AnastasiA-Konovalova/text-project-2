package org.example.model.paymentMethodRequest;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SecondaryRow;

@Getter
@Setter
public class PaymentMethodRequest {

    private TranData tran;

    //private OrderInfo order;
}
