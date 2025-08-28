package io.github.treeyw.crud.config.datasource.model;

import lombok.Data;

import java.lang.reflect.Field;

import static io.github.treeyw.crud.util.ObjectUtil.getEntity;

@Data
public class QueryTypeBO {
    @FieldComment("等于")
    public final static String EQUALS = "equals";
    @FieldComment("不等于")
    public final static String NE = "ne";
    @FieldComment("为空")
    public final static String NULL = "isNull";
    @FieldComment("不为空")
    public final static String NOT_NULL = "isNotNull";
    @FieldComment("英文逗号分割后in查询")
    public final static String SPLIT_IN = "split_in";
    @FieldComment("直接in(建议用上一种)")
    public final static String IN = "in";
    @FieldComment("前后模糊查询")
    public final static String LIKE = "like";
    @FieldComment("前模糊查询")
    public final static String LIKE_BEFOR = "like_befor";
    @FieldComment("后模糊查询")
    public final static String LIKE_AFTER = "like_after";
    @FieldComment("分割为多个值模糊查询")
    public final static String LIKE_OR = "like_or";
    @FieldComment("分割为多个值前模糊查询")
    public final static String LIKE_BEFOR_OR = "like_befor_or";
    @FieldComment("分割为多个值前后模糊查询")
    public final static String LIKE_AFTER_OR = "like_after_or";
    @FieldComment("大于")
    public final static String GT = "gt";
    @FieldComment("小于")
    public final static String LT = "lt";
    @FieldComment("大于等于")
    public final static String GE = "ge";
    @FieldComment("小于等于")
    public final static String LE = "le";
}
