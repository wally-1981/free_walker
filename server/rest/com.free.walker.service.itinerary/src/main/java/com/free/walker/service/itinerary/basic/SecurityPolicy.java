package com.free.walker.service.itinerary.basic;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SecurityPolicy {
    public enum SecurityLevel {
        HIGH, MEDIUM, LOW
    };

    public SecurityLevel level() default SecurityLevel.MEDIUM;
}
