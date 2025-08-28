// EntityManagerConfig.java（重构）
package io.github.treeyw.crud.config.datasource;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;

/**
 * @author treeyw
 * @description 实体管理器配置，提供获取 EntityManager 的方法
 * @date 2025/8/24 19:43
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EntityManagerConfig {

    private final EntityManagerRegistry registry;

    //获取默认数据源
    public EntityManager getEntityManager() {
        return registry.getEntityManager();
    }

    //获取数据源-class
    public EntityManager getEntityManager(Class<?> clazz) {
        return registry.getEntityManager(clazz);
    }

    //获取数据源-key
    public EntityManager getEntityManager(String dbKey) {
        return registry.getEntityManager(dbKey);
    }

    //获取数据源-key/class
    public EntityManager getEntityManager(String dbKey, Class<?> clazz) {
        return (dbKey != null) ? getEntityManager(dbKey) : getEntityManager(clazz);
    }



}
