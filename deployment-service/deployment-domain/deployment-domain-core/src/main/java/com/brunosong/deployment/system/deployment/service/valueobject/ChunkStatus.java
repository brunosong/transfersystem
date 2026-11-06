package com.brunosong.deployment.system.deployment.service.valueobject;

public enum ChunkStatus {
    WAITING,    //아직 보내지 않았다. 뼈대가 반영되기를 기다린다.
    SENT,       //보내기로 확정했다. 결과를 기다린다.
    APPLIED,    //대상이 반영했다고 알려 왔다.
    FAILED      //반영되지 않았다.
}
