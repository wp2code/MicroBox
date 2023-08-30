package com.wb2code.microbox.config.connect;

import com.wb2code.microbox.enums.DataSourceTypeEnum;

/**
 * @author liu_wp
 * @date 2020/11/18
 * @see
 */
public class JdbcServiceFactory {
    /**
     * @param jdbcSourceInfo
     * @return
     */
    public static JDBCService getJdbcService(JdbcSourceInfo jdbcSourceInfo) {
        if (jdbcSourceInfo.getDataSourceTypeEnum() == DataSourceTypeEnum.SQLITE) {
            return new SQLiteJDBCServiceImpl(jdbcSourceInfo);
        }
        return null;
    }


}
