package io.github.treeyw.crud.dao.jpa.manager;

import io.github.treeyw.crud.config.datasource.EntityManagerConfig;
import io.github.treeyw.crud.constant.SqlAttribute;
import io.github.treeyw.crud.dao.jpa.filter.DaoFiltel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * @author treeyw
 * @description jpa的实例持久化管理
 * @date 2025/8/24 19:43
 */
@Repository
@Transactional
public class CriteriaManager {
    Logger log = LoggerFactory.getLogger(CriteriaManager.class);
    @Autowired
    EntityManagerConfig entityManagerConfig;

    /**
     * @author treeyw
     * @description 根据实体类获取 EntityManager
     * @date 2025/8/24 19:43
     */
    public EntityManager getEntityManager(Class clazz) {
        return entityManagerConfig.getEntityManager(clazz);
    }

    public EntityManager getEntityManager() {
        return entityManagerConfig.getEntityManager();
    }

    public JpaManegerBO getEntityManagerSession(JpaEntityBO jb) {
        return getEntityManagerSession(jb.getClazz(), jb.getDbKey(), jb.getTableKey());
    }
    /**
     * @param clazz 实体类; dbKey 数据源 ,tableKey 分表键
     * @author treeyw
     * @description 如果参数带着dbKey,tableKey
     * 则可能改变原有的数据源/表名，例如分库分表
     * @date 2025/8/24 19:43
     */
    public JpaManegerBO getEntityManagerSession(Class clazz, String dbKey, String tableKey) {
        EntityManager em = entityManagerConfig.getEntityManager(dbKey, clazz);

        JpaManegerBO jm = new JpaManegerBO();
        Session session = em.unwrap(SessionImplementor.class);
        if (tableKey != null && !"".equals(tableKey)) {
            String srcTabmeName = SqlAttribute.SQL_CLASSNAME_AND_TABLENAME_MAP.get(clazz);
            DaoFiltel daoFiltel = new DaoFiltel(srcTabmeName, srcTabmeName + tableKey);  //替换表名
            session = session.getSessionFactory().withOptions().interceptor(daoFiltel).openSession();
        } else {
            jm.setCloseFlag(false);
        }

        jm.init(clazz, dbKey, tableKey);
        jm.setSession(session);
        jm.setManager(em);
        return jm;
    }

    public CriteriaManager() {
        super();
    }


    public <T> Object unique(final CriteriaQuery<T> result, JpaManegerBO jm) throws Exception {
        try {
            TypedQuery<T> typedQuery = jm.getManager().createQuery(result);
            typedQuery.setFirstResult(0);
            typedQuery.setMaxResults(2);
            Object obj = typedQuery.getSingleResult();
            jm.close();
            return obj;
        } catch (Exception e) {
            if (jm != null) jm.close();
        }
        return null;
    }

    public long count(final CriteriaQuery<Long> result, JpaManegerBO jm) throws Exception {
        Long l = 0L;
        try {
            Query query = jm.getManager().createQuery(result);
            List<Object> resultList = query.getResultList();
            if (!resultList.isEmpty()) {
                Object o = resultList.get(0);
                l = Long.parseLong(o.toString());
            }
            jm.close();
        } catch (Exception e) {
            e.printStackTrace();
            jm.close();
        }
        return l;
    }

    public <T> T save(T t, Boolean updateFlag, JpaManegerBO jm) {
        try {
            //修改
            if (updateFlag) {
                jm.getSession().merge(t);
            }
            //新增
            else {
                jm.getSession().persist(t);
            }
            return t;
        } catch (Exception err) {
            log.error(err.getMessage(), err);
            throw err;
        } finally {
            if (jm != null) jm.close();
        }
    }

    public static Class getCriterionClass(CriteriaQuery<?> query) {
        Root<?> root = query.getRoots().iterator().next(); // 获取查询的根实体
        return root.getJavaType(); // 获取根实体的类型
    }

}
