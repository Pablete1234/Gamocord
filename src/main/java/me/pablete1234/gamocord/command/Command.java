package me.pablete1234.gamocord.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String value();
    String usage();
    String description();
    boolean admin() default false;
}
