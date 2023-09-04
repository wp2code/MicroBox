package com.wb2code.microbox.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author lwp
 **/
@Getter
@RequiredArgsConstructor
public enum NgrokStatusEnum {
    /**
     * 未运行
     */
    UN_RUNNING(0),
    /**
     * 运行中
     */
    RUNNING(1),
    /**
     * 运行失败
     */
    RUN_FAIL(2);
    private final Integer status;

}
