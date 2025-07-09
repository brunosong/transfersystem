package com.brunosong.transfer.system.transfer.service.config.annotation;

import com.brunosong.transfer.system.transfer.service.valueobject.SourceType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SourceTypeSelector {
    SourceType[] sourceType(); // Default source type if not specified
}
