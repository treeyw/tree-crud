package io.github.treeyw.crud.service.common;

import io.github.treeyw.crud.config.datasource.EntityManagerConfig;
import io.github.treeyw.crud.constant.SqlAttribute;
import io.github.treeyw.crud.dao.jpa.manager.CriteriaManager;
import io.github.treeyw.crud.dao.jpa.manager.JpaEntityBO;
import io.github.treeyw.crud.dao.jpa.manager.JpaManegerBO;
import io.github.treeyw.crud.util.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.lang.reflect.Field;
import java.util.*;

import static io.github.treeyw.crud.config.datasource.CrudConfig.modelSaveBefor;
import static io.github.treeyw.crud.constant.ClassUtil.*;
import static io.github.treeyw.crud.constant.SqlAttribute.*;
import static io.github.treeyw.crud.util.CheckObjUtil.ckIsNotEmpty;
import static io.github.treeyw.crud.util.DateTool.dateTimeSec2Str;
import static io.github.treeyw.crud.util.ObjectUtil.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class ParentModifyService {
    Logger log = LoggerFactory.getLogger(ParentModifyService.class);

    @Autowired
    protected CriteriaManager daoCriteria;

    /**
     * @Description //根据自定义sql，执行修改、删除
     * 注：非固定结构的系统表尽量避免使用，维护成本会增加，建议在每个dao层分别实现
     * @Author treeyw
     * @Date 2020/9/10 16:12
     **/

    @Transactional
    @Modifying
    public long updateBySql(String sql, JpaManegerBO jm) {
        int i = 0;
        Session session = null;
        try {
            session = jm.getSession();
            i = session.createNativeQuery(sql).executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            if (session == null) return i;
            try {
                session.flush();
            } catch (Exception e2) {
                log.error(e2.getMessage(), e2);
            }
            session.close();
        }
        return i;
    }

    @Transactional
    @Modifying
    public long updateBySql(String sql) {
        return daoCriteria.getEntityManager().createNativeQuery(sql).executeUpdate();
    }

    @Transactional
    @Modifying
    public long updateBySql(String sql, Class c) {
        return daoCriteria.getEntityManager(c).createNativeQuery(sql).executeUpdate();
    }

    @Transactional
    public <T> int sysSave(T t) {
        String dbKey = getDbKeyByEntity(t, t.getClass());
        String tableKey = getTableKeyByEntity(t, t.getClass());

        JpaManegerBO jm = null;
        int objv = 0;
        try {
            jm = daoCriteria.getEntityManagerSession(t.getClass(), dbKey, tableKey);
            objv = jm.getSession().createNativeQuery(getInsertSql(t)).executeUpdate();
            jm.getSession().flush();
        } catch (Exception err) {
            if (jm != null) jm.close();
            log.error(err.getMessage(), err);
            throw err;
        } finally {
            if (jm != null) jm.close();
        }
        return objv;


    }


    /**
     * @Description //根据非空值进行update
     * where 参数不填 默认id修改，若填写则不用id修改，
     * 例如 " name='asdas' and lalal='babab' "
     * @Author treeyw
     * //
     **/
    @Transactional
    @Modifying
    public long update(Object obj, Class clazz) {
        String whereSql = " id='" + ObjectUtil.getEntity(obj, "id") + "'";
        return update(obj, whereSql, clazz);
    }

    public static List<String> saveBefor(Object t, Boolean updateFlag, String... sysUserBefor) {
        List<String> todoList = new ArrayList<>();
        //对参数内的字符串类型，避免单引号报错，改为''
        for (Field filed : t.getClass().getDeclaredFields()) {
            String cloum = filed.getName();
            if (ObjectUtil.getEntity(t, cloum) != null && filed.getType().getSimpleName().equals("String")) {
                ObjectUtil.setEntity(t, cloum, ObjectUtil.getEntity(t, cloum).toString().replaceAll("'", "''"));
            }
        }
        String userOpenid = (sysUserBefor != null && sysUserBefor.length > 0 && sysUserBefor[0] != null) ? sysUserBefor[0] : "";
        modelSaveBefor(updateFlag, t, userOpenid);
        return todoList;
    }


    /**
     * @Description //储存方法
     * @Author treeyw
     * @Date 2020/10/15 20:09
     * @Parameter --
     **/
    @Transactional
    public <T> T save(T t2, String... sysUser) throws Exception {
        Object id = ObjectUtil.getEntity(t2, "id");
        //如是否为修改方法，true为修改
        boolean updateFlag = ckIsNotEmpty(id);
        //如果id是-1则不用保存
        if (id != null && (id + "").equals("-1")) return t2;
        //写入属性赋默认值
        saveBefor(t2, updateFlag, sysUser);
        String dbKey = getDbKeyByEntity(t2, t2.getClass());
        String tableKey = getTableKeyByEntity(t2, t2.getClass());
        // ★ 用对应 dbKey 的事务管理器执行
        if (updateFlag) {
            update(t2, t2.getClass());
        } else {
            daoCriteria.save(t2, updateFlag, daoCriteria.getEntityManagerSession(t2.getClass(), dbKey, tableKey)
            );
        }
        return t2;
    }

    /**
     * @Description //根据非空值进行update,服务 save有id的情况
     * where 参数不填 默认id修改，若填写则不用id修改，
     * 例如 " name='asdas' and lalal='babab' "
     * @Author treeyw
     **/
    @Transactional
    @Modifying
    protected long update(Object obj, String where, Class clazz) {

        Map map = obj2Map(obj);
        String tableName = SQL_CLASSNAME_AND_TABLENAME_MAP.get(obj.getClass());
        StringBuilder sqlSb = new StringBuilder("update " + tableName + " set ");
        map.forEach((k, v) -> {
            if (v != null && !k.equals("id") && !checkTransient(clazz, k)) {

                //字段名优先@Column
                if (CLASS_AND_COLUMN.get(clazz.getName()) != null
                        && CLASS_AND_COLUMN.get(clazz.getName()).get(k) != null
                        && CLASS_AND_COLUMN.get(clazz.getName()).get(k).name() != null) {
                    k = CLASS_AND_COLUMN.get(clazz.getName()).get(k).name();
                } else k = hump2Line(k.toString());


                //时间格式单独处理
                if (v instanceof Date) {
                    //如果是时间是SQL_NULL_DATE，置空
                    if (dateTimeSec2Str((Date) v).equals(SqlAttribute.SQL_NULL_DATE))
                        sqlSb.append(k + " = null ,");
                    else
                        sqlSb.append(k + " = '" + dateTimeSec2Str((Date) v) + "' ,");
                } else if (v.toString().equals(SqlAttribute.SQL_NULL) || v.toString().equals(SqlAttribute.SQL_NULL_DATE)) {
                    sqlSb.append(k + " = null ,");
                } else {
                    sqlSb.append(k + " = '" + v + "' ,");
                }
            }
        });
        sqlSb.setLength(sqlSb.length() - 1);
        sqlSb.append(" where " + where);
        log.debug(sqlSb.toString());
        String dbKey = getDbKeyByEntity(obj, clazz);
        String tableKey = getTableKeyByEntity(obj, clazz);
        JpaManegerBO jm = null;
        int objv = 0;
        try {
            jm = daoCriteria.getEntityManagerSession(clazz, dbKey, tableKey);
            objv = jm.getSession().createNativeQuery(sqlSb.toString()).executeUpdate();
            jm.getSession().flush();
        } catch (Exception err) {
            if (jm != null) jm.close();
            log.error(err.getMessage(), err);
            throw err;
        } finally {
            if (jm != null) jm.close();
        }
        return objv;
    }

    /**
     * @author treeyw
     * @date 2024/4/18 16:57
     */
    public <T> Integer saveAll(Collection<T> tList, Class clazz) {
        return saveAll(tList, clazz, null, null);
    }

    public <T> Integer saveAll(Collection<T> tList, Class clazz, String dbKey, String tableKey, String... sysUser) {
        JpaEntityBO jb = new JpaEntityBO(clazz, dbKey, tableKey);
        int size = 0;
        JpaManegerBO jm = daoCriteria.getEntityManagerSession(jb);
        Session session = jm.getSession();
        //10W一批
        int batchSize = 10_0000;
        try {
            for (T t : tList) {
                modelSaveBefor(false, t, "");
                session.save(t);
                if (size++ % batchSize == 0) {
                    session.flush();
                    session.clear();
                }
            }
            session.flush();
            session.clear();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            jm.close();
        }
        return size;
    }


    /**
     * @Description //删除方法
     * @Author treeyw
     * @Date 2020/10/15 20:09
     * @Parameter --
     **/
    public Long parentDelete(Long id, Class clazz) {
        return parentDelete(id, clazz, null, null);
    }

    public Long parentDelete(Long id, Class clazz, String dbKey, String tableKey) {
        //如是否为修改方法，true为修改
        return updateBySql("update " + SQL_CLASSNAME_AND_TABLENAME_MAP.get(clazz) + "" +
                " set delete_flag=1,update_time=now() where id='" + id + "'", daoCriteria.getEntityManagerSession(clazz, dbKey, tableKey));
    }

    public Long parentDeleteList(List<Long> idList, Class clazz) {
        return parentDeleteList(idList, clazz, null, null);
    }

    public Long parentDeleteList(List<Long> idList, Class clazz, String dbKey, String tableKey) {
        if (idList == null || idList.size() == 0) return 0L;
        StringBuilder sb = new StringBuilder();
        idList.forEach(n -> sb.append(n + ","));
        sb.setLength(sb.length() - 1);
        //如是否为修改方法，true为修改
        return updateBySql("update " + SQL_CLASSNAME_AND_TABLENAME_MAP.get(clazz) + "" +
                " set delete_flag=1,update_time=now() where id in(" + sb.toString() + ")", daoCriteria.getEntityManagerSession(clazz, dbKey, tableKey));
    }

    /**
     * @Description //删除方法
     * @Author treeyw
     * @Date 2020/10/15 20:09
     * @Parameter --
     **/
    public Long parentDelete(Object obj) {
        Class clazz = obj.getClass();
        StringBuilder andSql = new StringBuilder();

        obj2Map(obj).forEach((k, v) -> {
            if (ckIsNotEmpty(v) && !checkTransient(clazz, k)) {
                andSql.append(" and ");
                if ("java.util.Date".equals(v.getClass().getName())) {
                    andSql.append(" " + hump2Line(k) + " ='" + dateTimeSec2Str((Date) v) + "'");
                } else {
                    andSql.append(" " + hump2Line(k) + " ='" + v + "'");
                }
            }
        });
        String dbKey = getDbKeyByEntity(obj, clazz);
        String tableKey = getTableKeyByEntity(obj, clazz);
        //如是否为修改方法，true为修改
        return updateBySql("update " + SQL_CLASSNAME_AND_TABLENAME_MAP.get(clazz) + "" +
                " set delete_flag=1,update_time=now()  where 1=1 " + andSql, daoCriteria.getEntityManagerSession(clazz, dbKey, tableKey));
    }

    /**
     * @Description //物理删除
     * @Author treeyw
     * @Date 2020/10/15 20:09
     * @Parameter --
     **/
    public Long sysDeleteById(Long id, Class clazz, String dbKey, String tableKey) {
        //如是否为修改方法，true为修改
        return updateBySql(
                "delete from " + SQL_CLASSNAME_AND_TABLENAME_MAP.get(clazz) + "" +
                        " where id='" + id + "'", daoCriteria.getEntityManagerSession(clazz, dbKey, tableKey));
    }

    /**
     * @Description //物理删除
     * @Author treeyw
     * @Date 2020/10/15 20:09
     * @Parameter --
     **/
    public Long sysDeleteById(Long id, Class clazz) {
        //如是否为修改方法，true为修改
        return sysDeleteById(id, clazz, null, null);
    }

    /**
     * @Description //物理删除
     * @Author treeyw
     * @Date 2020/10/15 20:09
     * @Parameter --
     **/
    public Long sysDeleteById(Object obj) {
        Class clazz = obj.getClass();
        StringBuilder andSql = new StringBuilder();
        obj2Map(obj).forEach((k, v) -> {
            if (ckIsNotEmpty(v) && !checkTransient(clazz, k)) {
                andSql.append(" and ");
                if ("java.util.Date".equals(v.getClass().getName())) {
                    andSql.append(" " + hump2Line(k) + " ='" + dateTimeSec2Str((Date) v) + "'");
                } else {
                    andSql.append(" " + hump2Line(k) + " ='" + v + "'");
                }
            }
        });
        String dbKey = getDbKeyByEntity(obj, clazz);
        String tableKey = getTableKeyByEntity(obj, clazz);
        //如是否为修改方法，true为修改
        return updateBySql("delete from  " + SQL_CLASSNAME_AND_TABLENAME_MAP.get(clazz) + "" +
                "  where 1=1 " + andSql, daoCriteria.getEntityManagerSession(clazz, dbKey, tableKey));
    }


    public static String getInsertSql(Object t) {
        String tableName = SQL_CLASSNAME_AND_TABLENAME_MAP.get(t.getClass());
        StringBuilder sqlSb = new StringBuilder("insert into " + tableName + " ");
        StringBuilder sqlKeys = new StringBuilder();
        StringBuilder sqlVals = new StringBuilder();
        Map map = obj2Map(t);
        map.forEach((k, v) -> {
            if (v != null && !checkTransient(t.getClass(), k)) {
                if (v instanceof Date) {
                    sqlVals.append("'" + dateTimeSec2Str((Date) v) + "',");
                } else {
                    sqlVals.append("'" + v + "',");
                }
                sqlKeys.append(hump2Line(k.toString().replace("ID", "id")) + ",");
            }
        });
        sqlKeys.setLength(sqlKeys.length() - 1);
        sqlVals.setLength(sqlVals.length() - 1);
        sqlSb.append("(" + sqlKeys + ") values (" + sqlVals + ")");
        return sqlSb.toString();
    }

}
