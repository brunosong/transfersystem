package com.brunosong.transfer.system.transfer.service;

import com.brunosong.transfer.system.transfer.service.entity.LearningMaterial;
import com.brunosong.transfer.system.transfer.service.entity.Transfer;

public abstract class TransferSendHelper {
    abstract void transferAction(Transfer transfer, LearningMaterial learningMaterial);
}
