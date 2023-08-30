package com.wb2code.microbox.config.connect;

import com.wb2code.microbox.annotation.Column;
import com.wb2code.microbox.annotation.IgnoreReflection;
import com.wb2code.microbox.annotation.Table;
import com.wb2code.microbox.enums.DataSourceTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据库连接信息
 *
 * @author liu_wp
 * @date 2020/11/17
 * @see
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "jdbc_source_info", uniqueKey = {"connect_name", "connect_host", "connect_port", "source_type", "init_db"})
public class JdbcSourceInfo extends BaseTableInfo {
    /**
     * 连接名称
     */
    @Column("connect_name")
    private String connectName;
    /**
     * 连接地址（域名或IP）
     */
    @Column("connect_host")
    private String connectHost;
    /**
     * 连接端口
     */
    @Column("connect_port")
    private Integer connectPort;
    /**
     * 用户名
     */
    @Column("user_name")
    private String userName;
    /**
     * 初始数据库
     */
    @Column("init_db")
    private String initDb;
    /**
     * 用户密码
     */
    @Column("password")
    private String password;
    @Column("source_type")
    private String sourceType;


    @IgnoreReflection
    private DataSourceTypeEnum dataSourceTypeEnum;


    @Override
    public JdbcSourceInfo clone() {
        try {
            JdbcSourceInfo jdbcSourceInfo = new JdbcSourceInfo();
            jdbcSourceInfo.setConnectName(this.getConnectName());
            jdbcSourceInfo.setConnectHost(this.getConnectHost());
            jdbcSourceInfo.setConnectPort(this.getConnectPort());
            jdbcSourceInfo.setUserName(this.getUserName());
            jdbcSourceInfo.setPassword(this.getPassword());
            jdbcSourceInfo.setInitDb(this.getInitDb());
            jdbcSourceInfo.setSourceType(this.getSourceType());
            jdbcSourceInfo.setDataSourceTypeEnum(this.getDataSourceTypeEnum());
            return jdbcSourceInfo;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return this.connectName;
    }


    public DataSourceTypeEnum getDataSourceTypeEnum() {
        if (this.sourceType != null) {
            return this.dataSourceTypeEnum = DataSourceTypeEnum.getDataSourceTypeEnum(this.sourceType);
        }
        return dataSourceTypeEnum;
    }

    public void setDataSourceTypeEnum(final DataSourceTypeEnum dataSourceTypeEnum) {
        if (null != dataSourceTypeEnum) {
            this.sourceType = dataSourceTypeEnum.typeName();
        }
        this.dataSourceTypeEnum = dataSourceTypeEnum;
    }


    public String getSourceType() {
        if (null != dataSourceTypeEnum) {
            return this.sourceType = dataSourceTypeEnum.typeName();
        }
        return sourceType;
    }

    public void setSourceType(final String sourceType) {
        if (this.sourceType != null) {
            this.dataSourceTypeEnum = DataSourceTypeEnum.getDataSourceTypeEnum(this.sourceType);
        }
        this.sourceType = sourceType;
    }
}
