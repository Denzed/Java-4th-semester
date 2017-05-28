package ru.spbau.daniil.smirnov.myjunit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates test methods
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    Class<? extends Throwable> expected() default None.class;
    String ignore() default "";

    class None extends Throwable {
        public None() {
        }
    }
}
