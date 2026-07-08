package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class OrderDetails {
    private Integer id;
    private String password;
    private String accessToken;
    private String status;
    private String cvv2AuthStatus;
}
