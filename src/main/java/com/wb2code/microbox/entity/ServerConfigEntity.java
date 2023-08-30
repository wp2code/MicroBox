package com.wb2code.microbox.entity;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.wb2code.microbox.annotation.*;
import com.wb2code.microbox.config.connect.BaseTableInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;

/**
 * @author lwp
 * @date 2022-10-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "server_config")
@QuerySql(sql = " order by update_time desc")
public class ServerConfigEntity extends BaseTableInfo {
    /**
     * 服务名称
     */
    @Column(value = "server_name")
    @Query(like = true)
    private String serverName;
    /**
     * 服务包地址
     */
    @Column(value = "server_jar_path")
    private String serverJarPath;
    /**
     * 服务包名称
     */
    @Query(like = true, accord = "or")
    @Column(value = "jar_name")
    private String jarName;
    /**
     * 服务配置
     */
    @Column(value = "server_config")
    private String serverConfig;
    /**
     * 服务网址
     */
    @Column(value = "web_site")
    private String webSite;
    /**
     * 状态：0-未运行；1-已运行；-1-运行失败
     */
    @Column(value = "status")
    private Integer status;
    /**
     * 运行PID
     */
    @Column(value = "pid")
    private Long pid;
    /**
     * 0-目录；1-文件
     */
    @Column(value = "type")
    private Integer type;

    @IgnoreReflection
    private String statusDesc;

    @IgnoreReflection
    private File dirPath;


    public String getStatusDesc() {
        if (StrUtil.isBlank(statusDesc) && status != null) {
            return status == 0 ? "未运行" : (status == 1 ? "运行中" : "运行失败");
        }
        return statusDesc;
    }

    /**
     * 实际启动包
     *
     * @return
     */
    public String getActualServerJarPath() {
        if (StrUtil.isNotBlank(this.jarName)) {
            return jarName;
        }
        return this.serverJarPath;
    }

    public String getFullServerJarPath() {
        if (StrUtil.isNotBlank(serverJarPath)) {
            if (FileUtil.isDirectory(serverJarPath) && StrUtil.isNotBlank(jarName)) {
                return serverJarPath + File.separator + jarName;
            }
            return serverJarPath;
        }
        return null;
    }

    public String getStatusDescColor() {
        if (status != null) {
            return status == 0 ? "#8a8a8a" : (status == 1 ? "#15c078" : "#E53333");
        }
        return null;
    }

    public File getDirPath() {
        if (FileUtil.isDirectory(serverJarPath)) {
            return dirPath = FileUtil.newFile(serverJarPath);
        }
        if (dirPath == null && FileUtil.isFile(serverJarPath)) {
            dirPath = FileUtil.newFile(serverJarPath).getParentFile();
        }
        return dirPath;
    }

    public String getJarName() {
        if (StrUtil.isBlank(jarName) && FileUtil.isFile(serverJarPath)) {
            jarName = FileUtil.getName(serverJarPath);
        }
        return jarName;
    }
}
