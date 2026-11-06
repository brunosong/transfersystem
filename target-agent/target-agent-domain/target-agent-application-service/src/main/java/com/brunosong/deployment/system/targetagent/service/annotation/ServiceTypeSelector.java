package com.brunosong.deployment.system.targetagent.service.annotation;

import com.brunosong.deployment.system.domain.valueobject.BrunoSongServiceType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServiceTypeSelector {
    BrunoSongServiceType type();
}
