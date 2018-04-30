package com.myththewolf.ServerButler.lib.command.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandPolicy {
    int userRequiredArgs() default -1;

    int consoleRequiredArgs() default -1;

    String commandUsage() default "<<Not defined>>";
}
