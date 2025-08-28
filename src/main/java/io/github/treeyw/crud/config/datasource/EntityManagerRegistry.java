// EntityManagerRegistry.java
package io.github.treeyw.crud.config.datasource;

import io.github.treeyw.crud.config.datasource.model.DataSourceDB;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.treeyw.crud.config.sys.SysConfig.sysPackagePath;

/**
 * @author treeyw
 * @description 实体管理器注册中心，负责管理多个数据源的 EntityManagerFactory 和事务管理器
 * @date 2025/8/24 19:43
 */
@Slf4j
public class EntityManagerRegistry {

    private final Map<String, EntityManagerFactory> emfMap;
    private final Map<String, PlatformTransactionManager> txMap;
    private static String defaultKey;
    /**
     * class -> dbKey
     */
    public static final Map<Class<?>, String> classUnitMap = new ConcurrentHashMap<>();
    /**
     * class -> tableName
     */
    private static final Map<Class<?>, String> classTableMap = new ConcurrentHashMap<>();

    public EntityManagerRegistry(Map<String, EntityManagerFactory> emfMap,
                                 Map<String, PlatformTransactionManager> txMap,
                                 String defaultKey) {
        this.emfMap = emfMap;
        this.txMap = txMap;
        this.defaultKey = defaultKey;
    }

    /**
     * 扫描实体，读取 @DataSourceDB 注解，建立映射
     */
    public static void scanEntities() {
        new Reflections(sysPackagePath).getTypesAnnotatedWith(jakarta.persistence.Entity.class, true)
                .forEach(clazz -> {
                    if (!clazz.isAnnotationPresent(jakarta.persistence.Table.class)) return;
                    if (clazz.isAnnotationPresent(DataSourceDB.class)) {
                        DataSourceDB ds = clazz.getAnnotation(DataSourceDB.class);
                        String key = ds.dataSource();
                        classUnitMap.put(clazz, key);
                        classTableMap.put(clazz, ds.tableName());
                    } else {
                        classUnitMap.put(clazz, defaultKey);
                        classTableMap.put(clazz, clazz.getName());
                    }
                });
        log.info("Entity scan finished: {} classes", classUnitMap.size());
    }

    /**
     * 取 EM by key（调用方注意关闭：建议只用于短期操作；多数情况下用 @Transactional 和线程绑定 EM）
     */
    public EntityManager getEntityManager(String key) {
        String use = (key != null) ? key : defaultKey;
        EntityManagerFactory emf = emfMap.get(use);
        if (emf == null) throw new IllegalArgumentException("No EMF for key=" + use);
        return EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
    }

    public EntityManager getEntityManager() {
        return getEntityManager(defaultKey);
    }

    /**
     * 根据实体类解析 key，再取 EM
     */
    public EntityManager getEntityManager(Class<?> clazz) {
        String key = classUnitMap.getOrDefault(clazz, defaultKey);
        return getEntityManager(key);
    }

    /**
     * 按 key 获取一个 TransactionTemplate（适合手动事务场景）
     */
    public TransactionTemplate tx(String key) {
        String use = (key != null) ? key : defaultKey;
        PlatformTransactionManager tm = txMap.get(use);
        if (tm == null) throw new IllegalArgumentException("No TxManager for key=" + use);
        return new TransactionTemplate(tm);
    }

}
