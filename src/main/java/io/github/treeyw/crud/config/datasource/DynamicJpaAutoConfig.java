// DynamicJpaAutoConfig.java
package io.github.treeyw.crud.config.datasource;

import io.github.treeyw.crud.dao.jpa.comment.CommentIntegrator;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.*;

import static io.github.treeyw.crud.config.datasource.CrudConfig.createDatabase;
import static io.github.treeyw.crud.config.datasource.EntityManagerRegistry.classUnitMap;
import static io.github.treeyw.crud.config.init.CrashStaticCrud.initSysConsoleYml;
import static io.github.treeyw.crud.config.sys.SysConfig.sysPackagePath;


/**
 * @author treeyw
 * @description 动态JPA配置，根据yml的 tree-crud.jpa.sources 配置动态创建多个数据源和实体管理器工厂
 * @date 2025/8/24 19:43
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DynamicJpaAutoConfig {
    private final ApplicationContext ctx;
    private final TreeCrudJpaProperties props;
    @Autowired
    ConfigurableEnvironment environment;

    /**
     * @author treeyw
     * @description 创建 EntityManagerRegistry，注册多个数据源和实体管理器工厂
     * @date 2025/8/24 19:43
     */
    @Bean
    @ConditionalOnMissingBean
    public EntityManagerRegistry entityManagerRegistry() {
        initSysConsoleYml(environment);
        EntityManagerRegistry.scanEntities();

        DefaultListableBeanFactory bf = (DefaultListableBeanFactory) ctx.getAutowireCapableBeanFactory();
        Map<String, EntityManagerFactory> emfMap = new HashMap<>();
        Map<String, PlatformTransactionManager> txMap = new HashMap<>();

        if (props.getSources().isEmpty()) {
            throw new IllegalStateException("tree-crud.jpa.sources is empty");
        }

        props.getSources().forEach((key, p) -> {
            // 预创建数据库
            createDatabase(p.getDbType(), p.getDbName(), p.getUrl(), p.getUsername(), p.getPassword());

            // DataSource
            String dsName = "ds_" + key;
            DataSource ds = DataSourceBuilder.create()
                    .driverClassName(p.getDriverClassName())
                    .url(p.getUrl())
                    .username(p.getUsername())
                    .password(p.getPassword())
                    .build();
            bf.registerBeanDefinition(dsName,
                    BeanDefinitionBuilder.genericBeanDefinition(DataSource.class, () -> ds)
                            .setScope(BeanDefinition.SCOPE_SINGLETON).getBeanDefinition());

            // 收集实体类
            List<Class<?>> entityClasses = new ArrayList<>();
            classUnitMap.forEach((clazz, dbKey) -> {
                if (dbKey.equals(key)) {
                    entityClasses.add(clazz);
                }
            });

            // JPA props
            Map<String, Object> jpaProps = new HashMap<>();
            String ddlAuto = (p.getHbm2ddlAuto() != null) ? p.getHbm2ddlAuto() : p.getDdlAuto();
            jpaProps.put("hibernate.hbm2ddl.auto", ddlAuto);
            if (p.getDialect() != null) jpaProps.put("hibernate.dialect", p.getDialect());
            jpaProps.put("hibernate.show_sql", p.getShowSql());
            jpaProps.put("hibernate.integrator_provider",
                    (IntegratorProvider) () -> Collections.singletonList(CommentIntegrator.INSTANCE));
            jpaProps.put("hibernate.physical_naming_strategy",
                    "io.github.treeyw.crud.config.datasource.init.PhysicalNamingStrategy");
            if (p.getJpa() != null) jpaProps.putAll(p.getJpa());

            // EntityManagerFactoryBuilder
            EntityManagerFactoryBuilder builder =
                    new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), jpaProps, null);

            LocalContainerEntityManagerFactoryBean emfBean = builder
                    .dataSource(ds)
                    .packages(entityClasses.toArray(new Class[0])) // ✅ 精确指定类
                    .persistenceUnit(key)
                    .build();
            emfBean.afterPropertiesSet(); // 初始化

            // 直接拿 Object 放到 map，不注册成 bean
            EntityManagerFactory emf = emfBean.getObject();
            emfMap.put(key, emf);

            // 注册 TxManager
            String txName = "tx_" + key;
            JpaTransactionManager tx = new JpaTransactionManager(emf);
            var txBd = BeanDefinitionBuilder
                    .genericBeanDefinition(PlatformTransactionManager.class, () -> tx)
                    .setScope(BeanDefinition.SCOPE_SINGLETON);
            if (key.equals(props.getDefaultKey())) {
                txBd.setPrimary(true);
            }
            bf.registerBeanDefinition(txName, txBd.getBeanDefinition());
            txMap.put(key, tx);
            log.info("Registered JPA unit '{}' with DataSource '{}', entities={}",
                    key, dsName, entityClasses.size());
        });

        // 默认 key
        String defaultKey = props.getDefaultKey();
        if (defaultKey == null || !emfMap.containsKey(defaultKey)) {
            defaultKey = props.getSources().keySet().iterator().next();
        }

        return new EntityManagerRegistry(emfMap, txMap, defaultKey);
    }

}
