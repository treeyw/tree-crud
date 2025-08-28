package io.github.treeyw.crud.model.demo;


import io.github.treeyw.crud.config.datasource.model.DataSourceDB;
import io.github.treeyw.crud.config.datasource.model.FieldComment;
import io.github.treeyw.crud.model.parent.ParentDO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

import java.util.Date;

@Entity
@Table(
        name = "treeyw_demo2"
)
@DataSourceDB(
        dataSource = "tool",
        tableName = "测试样例2"
)
@Data
public class TreeywDemo2DO extends ParentDO {
    @FieldComment("名称")
    private String name;
    @FieldComment("类型")
    private String type;

    @FieldComment("测试时间")
    private Date testDate;

    @FieldComment(value = "命令", dbType = "text")
    private String command;

    @FieldComment(value = "命令有填充", dbDefaultVal = "'默认命令填充'", length = 1000)
    private String commandFill;

    @Column(name = "id", insertable = false, updatable = false)
    @FieldComment(value = "模拟外键")
    private String oid;

    @Transient
    @FieldComment(value = "模拟外部对象", param = false)
    private TreeywDemo2DO oDemo;
}
