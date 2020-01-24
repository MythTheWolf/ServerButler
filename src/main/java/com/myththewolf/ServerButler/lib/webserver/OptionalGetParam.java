package com.myththewolf.ServerButler.lib.webserver;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(OptionalValues.class)
public @interface OptionalGetParam {
    ParamType requiredType() default ParamType.STRING;

    String name();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface OptionalValues {
    OptionalGetParam[] value();
}
