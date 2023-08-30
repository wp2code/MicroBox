package com.wb2code.microbox.config.connect;

import cn.hutool.core.util.StrUtil;
import com.wb2code.microbox.enums.DataSourceTypeEnum;
import com.wb2code.microbox.metadata.DbTableInfo;

import java.util.List;

/**
 * SQLite JDBC
 *
 * @author lwp
 * @date 2022-05-20
 */
public class SQLiteJDBCServiceImpl<T extends BaseTableInfo> extends AbstractJDBCService<T> {
    public SQLiteJDBCServiceImpl(final JdbcSourceInfo jdbcSourceInfo) {
        super(jdbcSourceInfo);
    }

    @Override
    protected DataSourceTypeEnum dataSourceTypeEnum() {
        return DataSourceTypeEnum.SQLITE;
    }

    @Override
    protected String createTableSql(String tableName, List<DbTableInfo> dbTableInfos) {
        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        sb.append(tableName);
        sb.append(" (");
        for (int i = 0; i < dbTableInfos.size(); i++) {
            final DbTableInfo dbTableInfo = dbTableInfos.get(i);
            sb.append(dbTableInfo.getColumn());
            sb.append(" ");
            sb.append(dbTableInfo.getJdbcType());
            if (dbTableInfo.getPrimaryKey() != null && dbTableInfo.getPrimaryKey()) {
                sb.append(" PRIMARY KEY ");
            }
            if (dbTableInfo.getAutoIncrement() != null && dbTableInfo.getAutoIncrement()) {
                sb.append(" AUTOINCREMENT ");
            }
            if (dbTableInfo.getCanNull() != null && !dbTableInfo.getCanNull()) {
                sb.append(" NOT NULL ");
            }
            if (StrUtil.isNotBlank(dbTableInfo.getDefaultValue())) {
                sb.append(" DEFAULT ");
                sb.append(dbTableInfo.getDefaultValue());
            }
            if (i != dbTableInfos.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }


}
