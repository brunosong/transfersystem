package com.brunosong.transfer.system.loadtarget.service.dto.create;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLoadTargetCommand {

    private String targetService;
    private String targetDb;
    private String type;
    private String dbUrl;
    private String userName;
    private String password;

}