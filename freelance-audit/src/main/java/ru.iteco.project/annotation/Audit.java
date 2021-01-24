package ru.iteco.project.annotation;

import java.lang.annotation.*;

/**
 * Аннотация для методов, для которых предусмотрен аудит
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Audit {

    String operation();

}
