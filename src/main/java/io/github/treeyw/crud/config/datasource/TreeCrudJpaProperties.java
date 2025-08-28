// TreeCrudJpaProperties.java
package io.github.treeyw.crud.config.datasource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author treeyw
 * @description TreeCrud JPA 配置属性类，用于配置多数据源和 JPA 相关属性
 * @date 2025/8/24 19:43
 */
@Data
@Component
@ConfigurationProperties(prefix = "tree-crud.jpa")
public class TreeCrudJpaProperties {

    /**
     * 可配置默认的数据源 key（不配则取 map 第一个）
     */
    private String defaultKey;

    /**
     * 多数据源：key -> props
     */
    private Map<String, JpaSourceProps> sources = new LinkedHashMap<>();

    @Data
    public static class JpaSourceProps {
        private String dbType;
        private String dbName;
        private String url;
        private String username;
        private String password;
        private String driverClassName;
        private Integer maxWait;
        private Integer minIdle;
        private Integer maxActive;
        private String ddlAuto = "none"; // update / none...
        private String dialect;         // 可选: org.hibernate.dialect.MySQL8Dialect
        private String showSql = "false";
        private String hbm2ddlAuto;     // 兼容老习惯
        private Map<String, String> jpa = new LinkedHashMap<>(); // 允许透传任意 jpa 属性
    }
}
