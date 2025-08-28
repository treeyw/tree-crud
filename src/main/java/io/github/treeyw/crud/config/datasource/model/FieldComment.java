package io.github.treeyw.crud.config.datasource.model;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldComment {
    //字段cName
    String value();

    //分表键
    boolean tableKey() default false;

    //分库键
    boolean dbKey() default false;

    //是否是密码
    boolean password() default false;

    //字段注释值
    int length() default 0;

    String dbDefaultVal() default "";

    String dbType() default "";

    //是否隐藏，false不隐藏
    boolean hide() default false;

    //展示给前端是否加密，false不加密
    boolean netEncryption() default false;

    //开头-结尾哪些免密，例如 123456，加密中间两位{2,2}则是头两位免密，后两位免密
    int[] netEncryptionBetweenAnd() default {0, 0};

    //入库是否加密，false不加密
    boolean dbEncryption() default false;

    String[] dct() default "";

    //Excel从左往右排列位置
    int col() default 0;

    //是否可输入参数
    boolean param() default true;

    //是否地区编码
    boolean distCode() default false;

    //是否单位编码
    boolean unitCode() default false;

    //是否必填
    boolean required() default false;
}
