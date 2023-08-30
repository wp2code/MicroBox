package com.wb2code.microbox.metadata;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lwp
 * @date 2022-11-16
 */
@Setter
@Getter
public class StatusItem {
    private String name;
    private Integer value;

    public StatusItem(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
