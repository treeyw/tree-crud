package io.github.treeyw.crud.config.datasource.model;

import lombok.Data;

@Data
public class QueryBO {
    @FieldComment(param = false, value = "查询方式")
    private String queryType;
    @FieldComment(param = false, value = "查询字段")
    private String queryColumn;
    @FieldComment(param = false, value = "查询值")
    private Object queryVal;

    public QueryBO() {
    }

    public QueryBO(String queryColumn, String queryType, Object queryVal) {
        this.queryType = queryType;
        this.queryColumn = queryColumn;
        this.queryVal = queryVal;
    }


}
