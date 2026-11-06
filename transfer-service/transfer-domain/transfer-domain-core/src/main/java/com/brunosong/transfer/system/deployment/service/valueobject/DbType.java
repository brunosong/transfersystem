package com.brunosong.transfer.system.deployment.service.valueobject;

import lombok.Getter;

@Getter
public enum DbType {
    MONGO("mongo"), MYSQL("mysql"), POSTGRESQL("postgresql"), ORACLE("oracle"), H2("h2");

    private final String name;

    DbType(String name) {
        this.name = name;
    }

}
