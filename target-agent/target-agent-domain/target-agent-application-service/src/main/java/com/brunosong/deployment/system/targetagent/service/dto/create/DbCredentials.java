package com.brunosong.deployment.system.targetagent.service.dto.create;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DbCredentials {
    private String dbUrl;
    private String userName;
    private String password;
}
