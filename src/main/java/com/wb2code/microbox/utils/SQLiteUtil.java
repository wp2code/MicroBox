package com.wb2code.microbox.utils;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wb2code.microbox.config.CommonConstants;
import com.wb2code.microbox.config.connect.BaseTableInfo;
import com.wb2code.microbox.config.connect.JDBCService;
import com.wb2code.microbox.config.connect.JdbcServiceFactory;
import com.wb2code.microbox.config.connect.JdbcSourceInfo;
import com.wb2code.microbox.entity.ServerConfigEntity;
import com.wb2code.microbox.enums.DataSourceTypeEnum;
import com.wb2code.microbox.metadata.DbTableInfo;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * @author lwp
 * @date 2022-10-12
 */
public class SQLiteUtil {
    private static final Log log = LogFactory.get();
    public static JDBCService SERVER;

    public static JDBCService getSingleton() {
        if (SERVER == null) {
            synchronized (SQLiteUtil.class) {
                if (SERVER == null) {
                    JdbcSourceInfo jdbcSourceInfo = new JdbcSourceInfo();
                    jdbcSourceInfo.setDataSourceTypeEnum(DataSourceTypeEnum.SQLITE);
                    jdbcSourceInfo.setConnectName(CommonConstants.SQLITE_DEFAULT_NAME);
                    jdbcSourceInfo.setPassword(CommonConstants.SQLITE_DEFAULT_PWD);
                    return SERVER = JdbcServiceFactory.getJdbcService(jdbcSourceInfo);
                }
            }
        }
        return SERVER;
    }

    /**
     * @param forceCreate
     * @return
     */
    public static boolean init(boolean forceCreate) {
        return createJDBCSourceTable(forceCreate) && createServerConfigTable(forceCreate);
    }

    /**
     * @param forceCreate
     * @return
     */
    public static boolean createJDBCSourceTable(boolean forceCreate) {
        final JDBCService jdbcService = SQLiteUtil.getSingleton();
        DbTableInfo c0 = new DbTableInfo("id", "INTEGER", "索引", true, true);
        DbTableInfo c1 = new DbTableInfo("connect_name", "VARCHAR(100)", "连接名称", false, false);
        DbTableInfo c2 = new DbTableInfo("connect_host", "VARCHAR(100)", "连接地址", false, false);
        DbTableInfo c3 = new DbTableInfo("connect_port", "INT2", "端口", false, false);
        DbTableInfo c4 = new DbTableInfo("user_name", "VARCHAR(100)", "用户名称", false, false);
        DbTableInfo c5 = new DbTableInfo("password", "VARCHAR(100)", "密码", false, false);
        DbTableInfo c6 = new DbTableInfo("source_type", "VARCHAR(50)", "类型", false, false);
        DbTableInfo c7 = new DbTableInfo("init_db", "VARCHAR(50)", "初始数据库", false, false);
        DbTableInfo c8 = new DbTableInfo("update_time", "INT8", "更新时间", false, false);
        List<DbTableInfo> tableInfoList = Arrays.asList(c0, c1, c2, c3, c4, c5, c6, c7, c8);
        return jdbcService.createTableIfAbsent(JdbcSourceInfo.class, tableInfoList, forceCreate);
    }

    /**
     * @param forceCreate
     * @return
     */
    public static boolean createServerConfigTable(boolean forceCreate) {
        final JDBCService jdbcService = SQLiteUtil.getSingleton();
        DbTableInfo c0 = new DbTableInfo("id", "INTEGER", "索引", true, true);
        DbTableInfo c1 = new DbTableInfo("server_name", "VARCHAR(100)", "服务名称", false, false);
        DbTableInfo c2 = new DbTableInfo("server_jar_path", "Text", "服务包地址", false, false);
        DbTableInfo c3 = new DbTableInfo("jar_name", "text", "服务包名称", false, false);
        DbTableInfo c4 = new DbTableInfo("web_site", "text", "服务网址", false, false);
        DbTableInfo c5 = new DbTableInfo("server_config", "text", "服务配置", false, false);
        DbTableInfo c6 = new DbTableInfo("pid", "INT8", "PID", false, false);
        DbTableInfo c7 = new DbTableInfo("status", "INT2", "状态：0-未运行；1-已运行；-1-运行失败", false, false);
        DbTableInfo c8 = new DbTableInfo("type", "INT2", "类型：0-目录；1-文件", false, false);
        DbTableInfo c9 = new DbTableInfo("update_time", "INT8", "更新时间", false, false);
        List<DbTableInfo> tableInfoList = Arrays.asList(c0, c1, c2, c3, c4, c5, c6, c7, c8, c9);
        return jdbcService.createTableIfAbsent(ServerConfigEntity.class, tableInfoList, forceCreate);
    }

    /**
     * @param object
     * @param <T>
     * @return
     */
    public static <T extends BaseTableInfo> List<T> select(T object) {
        try {
            return SQLiteUtil.getSingleton().select(object);
        } catch (Exception e) {
            DialogUtil.error(ExceptionUtil.getMessage(e));
            return null;
        }
    }

    /**
     * @param object
     * @param <T>
     * @return
     */
    public static <T extends BaseTableInfo> int selectCount(T object) {
        try {
            return SQLiteUtil.getSingleton().selectCount(object);
        } catch (Exception e) {
            DialogUtil.error(ExceptionUtil.getMessage(e));
            return 0;
        }
    }

    /**
     * @param id
     * @return
     */
    public static boolean delServerConfig(Long id) {
        try {
            return SQLiteUtil.getSingleton().deleteByPk(id, ServerConfigEntity.class);
        } catch (Exception e) {
            DialogUtil.error(ExceptionUtil.getMessage(e));
            return false;
        }
    }

    /**
     * @param obj
     * @param <T>
     * @return
     */
    public static <T extends BaseTableInfo> Result insertOrUpdate(T obj) {
        final Result result = new Result();
        boolean success = false;
        try {
            obj.setUpdateTime(System.currentTimeMillis());
            if (obj.getId() != null) {
                success = SQLiteUtil.getSingleton().updateByPk(obj);
            } else {
                success = SQLiteUtil.getSingleton().insert(obj);
            }
        } catch (Exception e) {
            result.setError(ExceptionUtil.getMessage(e));
        }
        result.setSuccess(success);
        return result;
    }

    @Data
    public static class Result<T> {
        private boolean isSuccess;
        private String error;
        private List<T> data;
    }

}
