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
// 查询参数有值时，自动按值拼接条件，例如 name=xx，page=1&pageSize=20&sort=id
@RequestMapping("list")
public ApiResult list(TreeywDemoDO to) throws Exception {
    // 额外补充查询条件
    to.addWhere("id", QueryTypeBO.GT, 0);
    return ApiResult.ok(parentQuery.listQuery(to));
}

// 查询列表（不执行 count，提高效率）
@RequestMapping("listSelect")
public ApiResult listSelect(TreeywDemoDO to) throws Exception {
    return ApiResult.ok(parentQuery.listQuery(to, false, true, "id,command"));
}

// 新增或修改（支持事务：无 id = 新增，有 id = 修改）
@Transactional
@RequestMapping("save")
public ApiResult save(TreeywDemoDO to) throws Exception {
    return ApiResult.ok(parentModify.save(to));
}

// 删除（逻辑删除，物理删除可用 parentModify.sysDeleteById）
@RequestMapping("del")
public ApiResult del(TreeywDemoDO to) throws Exception {
    return ApiResult.ok(parentModify.parentDelete(to));
}
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
