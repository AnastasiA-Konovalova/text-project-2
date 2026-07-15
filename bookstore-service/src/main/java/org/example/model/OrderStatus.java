package org.example.model;

public enum OrderStatus {
    PENDING("PENDING"),
    CARD_SAVED("CARD_SAVED"),
    FULLY_PAID("FULLY_PAID"),
    REFUNDED("REFUNDED");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static OrderStatus fromValue(String value) {
        for (OrderStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return PENDING;
    }
}
