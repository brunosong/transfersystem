package com.brunosong.transfer.system.datamigration.service.domain.valueobject;


public class DestinationDbCredentials {
    private String dbUrl;
    private String userName;
    private String password;

    public DestinationDbCredentials(String dbUrl, String userName, String password) {
        this.dbUrl = dbUrl;
        this.userName = userName;
        this.password = password;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
