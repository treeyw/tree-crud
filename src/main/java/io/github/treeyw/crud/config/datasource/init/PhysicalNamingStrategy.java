package io.github.treeyw.crud.config.datasource.init;

import io.github.treeyw.crud.util.ObjectUtil;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * @author treeyw
 * @description 自定义物理命名策略
 * @date 2025/8/24 19:43
 */
public class PhysicalNamingStrategy implements org.hibernate.boot.model.naming.PhysicalNamingStrategy {

    // 管理目录名的方法
    @Override
    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment context) {
        return apply(name);
    }

    // 管理模式名的方法
    @Override
    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment context) {
        return apply(name);
    }

    // 管理表名的方法
    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        return apply(name);
    }

    // 管理序列名的方法
    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment context) {
        return apply(name);
    }

    // 管理列名的方法
    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        if (name.getText().contains("$")) {
            return applyV2(name);
        }
        return apply(name);
    }

    /**
     * @description: 保留大小写
     * @author treeyw
     * @date 2024/4/17 17:58
     */
    private Identifier applyV2(Identifier name) {
        return Identifier.toIdentifier(name.getText().replace("$", ""));
    }


    // 通用方法，用于应用命名策略
    private Identifier apply(Identifier name) {
        if (name == null) {
            return null;
        } else {
            // 在这里添加你的自定义逻辑
            String newName = name.getText().replace(".", "_");
            return Identifier.toIdentifier(ObjectUtil.hump2Line(newName));
        }
    }
}
