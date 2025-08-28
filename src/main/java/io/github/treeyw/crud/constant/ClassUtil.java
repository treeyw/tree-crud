package io.github.treeyw.crud.constant;

import io.github.treeyw.crud.config.datasource.model.FieldComment;
import io.github.treeyw.crud.config.datasource.model.QueryBO;
import io.github.treeyw.crud.config.datasource.model.QueryTypeBO;
import io.github.treeyw.crud.model.parent.ParentDO;
import io.github.treeyw.crud.model.parent.bo.FieldCommentBO;
import io.github.treeyw.crud.util.ObjectUtil;

import java.lang.reflect.Field;
import java.util.*;

import static io.github.treeyw.crud.constant.SqlAttribute.*;
import static io.github.treeyw.crud.util.CheckObjUtil.ckIsNotEmpty;
import static io.github.treeyw.crud.util.DateTool.dateTimeSec2Str;
import static io.github.treeyw.crud.util.ObjectUtil.*;

public class ClassUtil {

    /**
     * 功能描述: 获取该实体类的分表键
     *
     * @author treeyw
     * @date
     */
    public static String getTableKeyByEntity(Object obj, Class clazz) {
        if (obj == null) return null;
        String key = SQL_CLASSNAME_AND_TABLEKEY_MAP.get(clazz);
        if (key == null) return null;
        Object val = ObjectUtil.getEntity(obj, key);
        if (val == null) return null;
        return val.toString();
    }

    /**
     * 功能描述: 获取该实体类的分库键
     *
     * @author treeyw
     * @date
     */
    public static String getDbKeyByEntity(Object obj, Class clazz) {
        if (obj == null) return null;
        String key = SQL_CLASSNAME_AND_DBKEY_MAP.get(clazz);
        if (key == null) return null;
        Object val = ObjectUtil.getEntity(obj, key);
        if (val == null) return null;
        return val.toString();
    }

    /**
     * e
     * 功能描述: 获取该类/某字段的fieldComment注解
     *
     * @author treeyw
     * @date
     */
    public static FieldComment getFieldComment(Class clazz, String field) {
        if (CLASS_AND_FIELDCOMMENT.get(clazz) == null
                || CLASS_AND_FIELDCOMMENT.get(clazz).get(field) == null)
            return ST_FIELDCOMMENT;
        return CLASS_AND_FIELDCOMMENT.get(clazz).get(field);
    }

    /**
     * 功能描述: 该类下该字段是否业务字段
     * 是业务字段，true,非业务字段，false
     *
     * @author treeyw
     * @date
     */
    public static Boolean checkTransient(Class clazz, Object field) {
        return checkTransient(clazz, field.toString());
    }

    public static Boolean checkTransient(Class clazz, String field) {
        if (CLASS_AND_TRANSIENT.get(clazz) == null)
            return CLASS_AND_TRANSIENT.get(ParentDO.class).contains(field);
        return CLASS_AND_TRANSIENT.get(clazz).contains(field);
    }

    public final static FieldComment ST_FIELDCOMMENT = new FieldCommentBO();
    private static List<String> obj2SqlNoAppendKey = Arrays.asList("page,pageSize,sordcloum,sord".split(","));

    /**
     * @author treeyw
     * @description 把对象转换成sql的where条件
     * @date 2025/8/24 19:43
     */
    public static String obj2WhereSql(Object obj) throws Exception {
        StringBuilder andSql = new StringBuilder();
        Map<String, Object> map = obj2Map(obj);
        map.forEach((k, v) -> {
            if (ckIsNotEmpty(v) && !obj2SqlNoAppendKey.contains(k) && !checkTransient(obj.getClass(), k)) {
                andSql.append(" and ");
                if ("java.util.Date".equals(v.getClass().getName())) {
                    andSql.append(" " + hump2Line(k) + " ='" + dateTimeSec2Str((Date) v) + "'");
                } else {
                    andSql.append(" " + hump2Line(k) + " ='" + v + "'");
                }
            }
        });

        List<QueryBO> list = (List<QueryBO>) getEntity(obj, "whereList");
        if (list != null) list.forEach(n -> {
            if (!ckIsNotEmpty(n.getQueryColumn())) return;
            String column = " and " + hump2Line(n.getQueryColumn()) + " ";
            if (QueryTypeBO.IN.equals(n.getQueryType())) {
                StringBuilder xsb = new StringBuilder();
                if (n.getQueryVal() != null) {
                    ((List) n.getQueryVal()).forEach(v -> xsb.append("'" + v + "',"));
                    //如果list没循环出来结果，直接跳过这个column
                    if (xsb.length() == 0) return;
                    xsb.setLength(xsb.length() - 1);
                }
                andSql.append(column + "(" + xsb.toString() + ")");
            } else if (QueryTypeBO.SPLIT_IN.equals(n.getQueryType())) {
                StringBuilder xsb = new StringBuilder();
                if (n.getQueryVal() != null) {
                    for (String v : n.getQueryVal().toString().split(",")) {
                        xsb.append("'" + v + "',");
                    }
                    //如果list没循环出来结果，直接跳过这个column
                    if (xsb.length() == 0) return;
                    xsb.setLength(xsb.length() - 1);
                }
                andSql.append(column + "(" + xsb.toString() + ") ");
            } else if (QueryTypeBO.GT.equals(n.getQueryType())) {
                andSql.append(column + " > '" + n.getQueryVal() + "' ");
            } else if (QueryTypeBO.GE.equals(n.getQueryType())) {
                andSql.append(column + " >= '" + n.getQueryVal() + "' ");
            } else if (QueryTypeBO.LT.equals(n.getQueryType())) {
                andSql.append(column + " < '" + n.getQueryVal() + "' ");
            } else if (QueryTypeBO.LE.equals(n.getQueryType())) {
                andSql.append(column + " <= '" + n.getQueryVal() + "' ");
            } else if (QueryTypeBO.NE.equals(n.getQueryType())) {
                andSql.append(column + " != '" + n.getQueryVal() + "' ");
            } else if (QueryTypeBO.LIKE_AFTER.equals(n.getQueryType())) {
                andSql.append(column + " like '" + n.getQueryVal() + "%' ");
            } else if (QueryTypeBO.LIKE_BEFOR.equals(n.getQueryType())) {
                andSql.append(column + " like '%" + n.getQueryVal() + "' ");
            } else if (QueryTypeBO.NULL.equals(n.getQueryType())) {
                andSql.append(column + " is null ");
            } else if (QueryTypeBO.NOT_NULL.equals(n.getQueryType())) {
                andSql.append(column + " is not null ");
            } else
                andSql.append(column + n.getQueryType() + " '" + n.getQueryVal() + "'");
        });


        return andSql.toString();
    }

    public static Map<String, FieldComment> class2FieldComment(Class clazz) {
        Map<String, FieldComment> map = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(FieldComment.class)) {
                FieldComment fd = field.getAnnotation(FieldComment.class);
                map.put(field.getName(), fd);
            }
        }
        try {
            for (Field field : clazz.getSuperclass().getDeclaredFields()) {
                if (field.isAnnotationPresent(FieldComment.class)) {
                    FieldComment fd = field.getAnnotation(FieldComment.class);
                    map.put(field.getName(), fd);
                }
            }
        } catch (Exception e) {
        }

        return map;
    }

    public static Map<String, Class> class2FieldType(Class clazz) {
        Map<String, Class> map = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            map.put(field.getName(), field.getType());
        }
        try {
            for (Field field : clazz.getSuperclass().getDeclaredFields()) {
                map.put(field.getName(), field.getType());
            }
        } catch (Exception e) {
        }
        return map;
    }

}
