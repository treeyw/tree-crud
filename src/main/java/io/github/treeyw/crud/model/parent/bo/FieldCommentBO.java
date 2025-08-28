package io.github.treeyw.crud.model.parent.bo;


import io.github.treeyw.crud.config.datasource.model.FieldComment;

import java.lang.annotation.Annotation;


public class FieldCommentBO implements FieldComment {
    public boolean equals(Object o) {
        return false;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return null;
    }

    public Class<? extends Annotation> annotationType() {
        return null;
    }

    //分表键
    public boolean tableKey() {
        return false;
    }

    //分库键
    public boolean dbKey() {
        return false;
    }

    //数据库内的默认值
    public String dbDefaultVal() {
        return "";
    }

    //数据库内字段的类型
    public String dbType() {
        return "";
    }

    //是否是密码
    public boolean password() {
        return false;
    }

    //字段注释值
    public String value() {
        return null;
    }

    //字段注释值
    public int length() {
        return 0;
    }

    //是否隐藏，false不隐藏
    public boolean hide() {
        return false;
    }

    //展示给前端是否加密，false不加密
    public boolean netEncryption() {
        return false;
    }

    //开头-结尾哪些免密，例如 123456，加密中间两位{2,2}则是头两位免密，后两位免密
    public int[] netEncryptionBetweenAnd() {
        return new int[]{0, 0};
    }

    //入库是否加密，false不加密
    public boolean dbEncryption() {
        return false;
    }

    public String[] dct() {
        return "".split(",");
    }

    //Excel从左往右排列位置
    public int col() {
        return 0;
    }

    //是否文件
    public boolean param() {
        return true;
    }

    //是否地区编码
    public boolean distCode() {
        return false;
    }

    //是否单位编码
    public boolean unitCode() {
        return false;
    }
    //是否必填
    public boolean required() {
        return false;
    }


}
