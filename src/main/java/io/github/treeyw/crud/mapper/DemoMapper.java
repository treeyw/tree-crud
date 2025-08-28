package io.github.treeyw.crud.mapper;

public class DemoMapper {
    public static final String sqlWhere = """
            and id = #{id}
            and type = '#{type}'
            and id < #{id}
            and id > #{id}
            """;
    public static final String demoSql = """
            select count(*)
            from treeyw_demo
            where 1=1
            """ + sqlWhere + """
            """;

    public static final String demoSql2 = """
            select count(*)
            from treeyw_demo
            where 1=1
            """ + sqlWhere + """
            """;

}
