package com.myththewolf.ServerButler.lib.webserver;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(RequiredValues.class)
public @interface RequiredGETParam {
    ParamType requiredType() default ParamType.STRING;

    String name();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface RequiredValues {
    RequiredGETParam[] value();
}
