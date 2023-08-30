package com.wb2code.microbox.annotation;

import java.lang.annotation.*;

/**
 * @author :lwp
 * @date :Created in 2022-05-21
 */

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
    /**
     * @return
     */
    String value();

    /**
     * @return
     */
    boolean pk() default false;

    /**
     * 是否自增
     *
     * @return
     */
    boolean isAutoIncr() default false;

}
