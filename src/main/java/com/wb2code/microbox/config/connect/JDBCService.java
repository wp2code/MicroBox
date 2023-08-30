package com.wb2code.microbox.config.connect;

import com.wb2code.microbox.metadata.DbTableInfo;

import java.util.List;

/**
 * @author lwp
 * @date 2022-10-12
 */
public interface JDBCService<T> {
    /**
     * 新建表
     *
     * @param cls          表对应的实体类
     * @param dbTableInfos 表字段信息
     * @param forceCreate  是否强制创建
     * @return
     */
    boolean createTableIfAbsent(Class<T> cls, List<DbTableInfo> dbTableInfos, boolean forceCreate);

    /**
     * 检查表是否存在
     *
     * @param tableName
     * @return
     */
    boolean checkTableIsExist(String tableName);

    /**
     * 新增
     *
     * @param obj
     * @return
     */
    boolean insert(T obj);

    /**
     * 根据主键修改数据
     *
     * @param obj
     * @return
     */
    boolean updateByPk(T obj);

    /**
     * 查询所有数据
     *
     * @return
     */
    List<T> select(T obj);


    /**
     * 查询所有数据数量
     *
     * @param obj
     * @return
     */
    int selectCount(T obj);

    /**
     * 根据主键删除数据
     *
     * @param primaryKey
     * @param cls
     * @return
     */
    boolean deleteByPk(Long primaryKey, Class<T> cls);
}
