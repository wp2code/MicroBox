package com.wb2code.microbox.config.connect;


import com.wb2code.microbox.annotation.Column;
import lombok.Data;

import java.io.Serializable;

/**
 * @author liu_wp
 * @date 2020/11/18
 * @see
 */
@Data
public class BaseTableInfo implements Cloneable, Serializable {
    @Column(value = "id", pk = true, isAutoIncr = true)
    private Long id;
    /**
     *  更新时间
     */
    @Column(value = "update_time")
    private Long updateTime;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
