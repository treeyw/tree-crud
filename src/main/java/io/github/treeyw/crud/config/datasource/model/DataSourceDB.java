package io.github.treeyw.crud.config.datasource.model;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSourceDB {
    //数据库选择
    String dataSource() default "";

    //表名
    String tableName() default "";

    //备注
    String remark() default "";
}
