package com.brunosong.transfer.system.transfer.service.valueobject;

public enum TransType {
    API("API"),
    MESSAGING("Messaging");

    private final String description;

    TransType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
