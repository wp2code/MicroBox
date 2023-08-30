package com.wb2code.microbox.annotation;

import java.lang.annotation.*;

/**
 * @author :lwp
 * @date :Created in 2022-05-21
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QuerySql {
    String sql() default "";
}
