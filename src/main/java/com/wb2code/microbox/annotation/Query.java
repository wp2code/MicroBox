package com.wb2code.microbox.annotation;

import java.lang.annotation.*;

/**
 * @author :lwp
 * @date :Created in 2022-05-21
 */

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Query {
    /**
     * @return
     */
    boolean like() default false;

    /**
     * 连接符合
     *
     * @return
     */
    String accord() default "and";

}
