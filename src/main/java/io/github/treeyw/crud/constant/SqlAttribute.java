package io.github.treeyw.crud.constant;

import io.github.treeyw.crud.config.datasource.model.FieldComment;
import io.github.treeyw.crud.util.DateTool;

import jakarta.persistence.Column;
import java.util.*;

public final class SqlAttribute {

    private SqlAttribute() {
    }

    //第几页
    public final static String SQL_PAGE = "page";

    //每页数量
    public final static String SQL_PAGESIZE = "pageSize";
    //排序字段
    public final static String SQL_SORDCLOUM = "sordcloum";
    //排序方式，desc,asc
    public final static String SQL_SORD = "sord";
    public final static String SQL_GROUPBY = "groupBys";

    public final static String SQL_TOKEN = "token";
    //占位符null
    public final static String SQL_NULL = "-88888888";
    public final static Integer SQL_NULL_INT = -88888888;
    //占位符null
    public final static String SQL_NULL_DATE = "1111-11-11 11:11:11";
    public final static Date SQL_NULL_DATE_D = DateTool.str2DateTimeSec("1111-11-11 11:11:11");


    //默认一页的数量，系统启动时根据配置文件修改
    public static int PAGESIZE = 20;

    //sql删除字段
    public final static String SQL_DELETEFLAG = "deleteFlag";


    //表名实体类映射
    public static Map<Class, String> SQL_CLASSNAME_AND_TABLENAME_MAP = new HashMap();
    public static Map<String, Class> SQL_TABLENAME_AND_CLASSNAME_MAP = new HashMap();
    //该表的分表键、分库键
    public static Map<Class, String> SQL_CLASSNAME_AND_TABLEKEY_MAP = new HashMap();
    public static Map<Class, String> SQL_CLASSNAME_AND_DBKEY_MAP = new HashMap();


    public static Map<Class, Map<String, FieldComment>> CLASS_AND_FIELDCOMMENT = new HashMap();
    public static Map<Class, Map<String, Column>> CLASS_AND_COLUMN = new HashMap();

    public static Map<Class, List<String>> CLASS_AND_CLOUM = new HashMap();
    public static Map<Class, List<String>> CLASS_AND_TRANSIENT = new HashMap();


}
