package com.wb2code.microbox.metadata;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lwp
 * @date 2022-10-12
 */
@Data
public class DbTableInfo implements Serializable {
    private String column;
    private String jdbcType;
    private String comment;
    private String defaultValue;
    private Boolean canNull;
    private Boolean autoIncrement;
    private Boolean primaryKey;
    public DbTableInfo(String column, String jdbcType, String comment, Boolean primaryKey, Boolean autoIncrement) {
        this.column = column;
        this.jdbcType = jdbcType;
        this.comment = comment;
        this.primaryKey = primaryKey;
        this.autoIncrement = autoIncrement;
    }
}
