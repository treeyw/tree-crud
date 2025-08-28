# Tree-CRUD

**Tree-CRUD** 是一个轻量级的快速开发工具包，基于 **JPA 实体**，通过 **反射与字节码增强**，自动生成 **DAO、Service、Controller** 层。  
只需专注业务逻辑，无需重复编写 CRUD 样板代码。

---

## ✨ 特性

- 🚀 **零样板**：只需编写实体类，CRUD 层自动生成  
- 🔗 **多数据源支持**：通过注解 `@DataSourceDB` 指定数据源  
- 📝 **字段注释 & 默认值**：`@FieldComment` 可绑定数据库注释、类型、默认值  
- 🔄 **自动查询**：参数有值则自动拼接查询条件  
- 📦 **开箱即用**：支持多数据源、分页、排序、事务、动态查询、物理/逻辑删除  

---

## 🛠 使用方式


### 1. 控制器示例
```java
TreeywDemoDO query = new TreeywDemoDO();
query.setId(5L);
to.addWhere("id", QueryTypeBO.GT, 0);
to.addWhere("type", QueryTypeBO.LIKE, "模糊");
//默认查询count/list，自动多页场景下分页优化
ListVO listVO=parentQuery.listQuery(to);
//可单独查询count
listVO=parentQuery.listQuery(to,true,false);
//可单独查询list
listVO=parentQuery.listQuery(to,false,true);
//查询指定字段
listVO=parentQuery.listQuery(to,true,true,"id,name,type");

//新增或修改 支持事务，无id新增，有id修改
TreeywDemoDO demo = new TreeywDemoDO();
parentModify.save(demo);

//逻辑删除
parentModify.parentDelete(1L,TreeywDemoDO.class)
//物理删除
parentModify.sysDelete(1L,TreeywDemoDO.class)
```

### 2. 定义实体类
```java
@Entity
@Table(name = "treeyw_demo")
@DataSourceDB(dataSource = "main", tableName = "测试样例") //此处dataSource对应yml里的数据源，不写则为默认
@Data
public class TreeywDemoDO extends ParentDO {

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
    private TreeywDemoDO oDemo;
}
```
