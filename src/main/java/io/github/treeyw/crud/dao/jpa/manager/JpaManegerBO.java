package io.github.treeyw.crud.dao.jpa.manager;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import jakarta.persistence.EntityManager;

/**
 * @author treeyw
 * @description jpa的持久化对象管理，用于封装EntityManager和Session等
 * @date 2025/8/24 19:43
 */
@Slf4j
@Data
public class JpaManegerBO {
    private Class clazz;
    private String dbKey;
    private String tableKey;
    private Boolean closeFlag = true;
    private EntityManager manager;
    private Session session;

    public void init(Class clazzT, String dbKeyT, String tableKeyT) {
        clazz = clazzT;
        dbKey = dbKeyT;
        tableKey = tableKeyT;
    }

    public void close() {
        //默认只clean
        try {
            if (session != null && session.isOpen() && manager != null && manager.isOpen())
                manager.clear();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        try {
            if (session != null) {
                session.clear();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        //分库分表的情况下要close
        if (closeFlag) {
            try {
                if (session != null) {
                    session.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
