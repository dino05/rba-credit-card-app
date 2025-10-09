package com.rba.creditcardapp.model;

public enum CardStatus {
    PENDING,
    APPROVED,
    REJECTED,
    IN_PROGRESS,
    COMPLETED,
    SHIPPED;

    public static boolean isValid(String status) {
        if (status == null) return false;
        try {
            valueOf(status.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static CardStatus fromString(String status) {
        if (status == null) return null;
        try {
            return valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}