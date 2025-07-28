package com.brunosong.transfer.system.datamigration.service.annotation;

import com.brunosong.transfer.system.domain.valueobject.BrunoSongServiceType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TargetType {
    BrunoSongServiceType type();
}
