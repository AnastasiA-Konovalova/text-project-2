package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateOrderResponse {
    private boolean success;
    private Integer id;
    private String password;
    private String accessToken;
    private String status;
    private String cvv2AuthStatus;
    private String message;

        public CreateOrderResponse(boolean success,
                                   OrderResponse response,
                                   String message) {

            this.success = success;

            if (response != null) {
                this.id = Integer.valueOf(response.getOrder().getId());
                this.password = response.getOrder().getPassword();
                this.accessToken = response.getOrder().getAccessToken();
                this.status = response.getOrder().getStatus();
                this.cvv2AuthStatus = response.getOrder().getCvv2AuthStatus();
            }

            this.message = message;
        }
    }





