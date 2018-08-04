package com.janeullah.healthinspectionrecords.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * https://dzone.com/articles/creating-custom-annotations-in-java
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogMethodExecutionTime {
    String value() default "";
}
