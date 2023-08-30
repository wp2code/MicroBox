package com.wb2code.microbox.annotation;

import java.lang.annotation.*;

/**
 * @author :lwp
 * @date :Created in 2022-05-21
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    String value();
    String schema() default "";
    /**
     * 聚合索引
     *
     * @return
     */
    String[] uniqueKey() default {};
}
