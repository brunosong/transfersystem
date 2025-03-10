package com.brunosong.transfer.system.transfer.service.dataaccess.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DbConfig {
    private DbType dbType;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private String tableOrCollection; // MySQL 테이블 또는 MongoDB 컬렉션
}
