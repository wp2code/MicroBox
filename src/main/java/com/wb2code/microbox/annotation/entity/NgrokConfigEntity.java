package com.wb2code.microbox.annotation.entity;

import com.wb2code.microbox.annotation.Column;
import com.wb2code.microbox.annotation.Table;
import com.wb2code.microbox.config.connect.BaseTableInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author lwp
 * @date 2023-08-18
 **/
@Data
@Table(value = "ngrok_config")
@EqualsAndHashCode(callSuper = true)
public class NgrokConfigEntity extends BaseTableInfo {
    @Column(value = "public_url")
    private String publicUrl;
    @Column(value = "domain")
    private String domain;
    @Column(value = "port")
    private Integer port;
    @Column(value = "auth_token")
    private String authToken;
    @Column(value = "api_access_token")
    private String apiAccessToken;
    @Column(value = "ext_config")
    private String extConfig;
    /**
     * 状态：0-未运行；1-已运行；-1-运行失败
     */
    @Column(value = "status")
    private Integer status;
    /**
     * 通道名称
     */
    @Column(value = "tunnel_name")
    private String tunnelName;
}
