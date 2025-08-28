package io.github.treeyw.crud.model.parent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.treeyw.crud.config.datasource.model.FieldComment;
import io.github.treeyw.crud.config.datasource.model.QueryBO;
import io.github.treeyw.crud.config.datasource.model.QueryTypeBO;
import io.github.treeyw.crud.constant.SqlAttribute;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import static io.github.treeyw.crud.constant.ClassUtil.obj2WhereSql;
import static io.github.treeyw.crud.util.CheckObjUtil.ckIsNotEmpty;
import static io.github.treeyw.crud.util.ObjectUtil.getEntity;
import static io.github.treeyw.crud.util.ObjectUtil.setEntity;

@MappedSuperclass
@Data
public class ParentDO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 18)
    @FieldComment("主键id")
    protected Long id;

    @Temporal(value = TemporalType.TIMESTAMP)
    @FieldComment(value = "创建时间", param = false)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")

    protected Date createTime;            /* 记录创建时间 */
    @Temporal(value = TemporalType.TIMESTAMP)
    @FieldComment(value = "修改时间", param = false)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    protected Date updateTime;             /* 记录最新的修改时间 */

    @FieldComment(value = "创建人员", hide = true, param = false)
    protected String createUserid;             /* 记录创建人员id */
    @Transient
    @FieldComment(value = "创建人员姓名", hide = true, param = false)
    protected String createUserName;             /* 记录创建人员姓名 */

    @FieldComment(value = "是否删除0否1是", hide = true, param = false)
    protected Integer deleteFlag;                /* 是否删除 */

    @Transient
    @JSONField(serialize = false)
    @JsonIgnore
    @FieldComment(value = "页码", hide = true, param = false)
    public int page = 1;
    @Transient
    @JSONField(serialize = false)
    @JsonIgnore
    @FieldComment(value = "一页数量", hide = true, param = false)
    public int pageSize = SqlAttribute.PAGESIZE;

    @Transient
    @JSONField(serialize = false)
    @JsonIgnore
    @FieldComment(value = "排序字段", hide = true, param = false)
    public String sortField = "updateTime,id";
    @Transient
    @JSONField(serialize = false)
    @JsonIgnore
    @FieldComment(value = "排序方式", hide = true, param = false)
    public String sortWay = "desc";

    @Transient
    @FieldComment(value = "创建人员对象", param = false)
    protected Object createUser;

    @Transient
    @JSONField(serialize = false)
    @JsonIgnore
    @FieldComment(value = "一些非equs的查询方式", hide = true, param = false)
    protected List<QueryBO> whereList;
    @Transient
    @JSONField(serialize = false)
    @JsonIgnore
    @FieldComment(value = "一些非equs的查询方式JSON", hide = true, param = false)
    protected String whereListJson;

    @Transient
    @JSONField(serialize = false)
    @JsonIgnore
    @FieldComment(value = "一些非自定义的查询方式", hide = true, param = false)
    protected List<Predicate> predicateList;

    @Transient
    @JSONField(serialize = false)
    @JsonIgnore
    @FieldComment(value = "一些groupBy的值", hide = true, param = false)
    protected String groupBys;

    public ParentDO() {
    }

    public void setCreateUserid(String uid) {
        this.createUserid = uid;
    }

    public void setCreateUserid(Long uid) {
        this.createUserid = uid + "";
    }

    public String getSortField() {
        if (ckIsNotEmpty(sortField)) {
            List<String> sordList = Arrays.asList(sortField.split(","));
            //排序條件裏如果沒有ID，則補充一個ID
            if (!sordList.contains("id"))
                sortField = sortField + ",id";
            if (!sordList.contains("createTime"))
                sortField = sortField + ",createTime";
            if (!sordList.contains("updateTime"))
                sortField = sortField + ",updateTime";
        }
        return sortField;
    }

    public ParentDO userid2UserDO(String useridKey, String userObjKey) {
        return this;
    }


    public void setWhereListJson(String whereListJson) {
        this.whereListJson = whereListJson;
        if (whereListJson != null && whereList == null) {
            try {
                whereList = JSON.parseArray(whereListJson, QueryBO.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<QueryBO> getWhereList() {
        if (ckIsNotEmpty(whereListJson) && whereList == null) {
            try {
                whereList = JSON.parseArray(whereListJson, QueryBO.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return whereList;
    }



    //queryType取自QueryTypeBO内的值
    //key是要查的字段
    //val是要查的值
    public ParentDO addWhere(String queryColumn, String queryType, Object queryVal) {
        if (getWhereList() == null) whereList = new ArrayList<>();
        whereList.add(new QueryBO(queryColumn, queryType, queryVal));
        return this;
    }

    //queryType取自QueryTypeBO内的值
    //key是要查的字段
    //val是要查的值
    public ParentDO addWhere(String queryColumn, String queryType) {
        addWhere(queryColumn, queryType, null);
        return this;
    }

    public ParentDO removeWhere(String queryColumn, String queryType, Object queryVal) {
        int rm = -1;
        for (int i = 0; i < whereList.size(); i++) {
            QueryBO queryBO = whereList.get(i);
            if (queryBO.getQueryColumn().equals(queryColumn) &&
                    queryBO.getQueryType().equals(queryType) &&
                    (queryBO.getQueryVal() + "").equals(queryVal + "")
            ) {
                rm = i;
                break;
            }
        }
        if (rm == -1) return this;
        else whereList.remove(rm);
        return removeWhere(queryColumn, queryType, queryVal);

    }

    public ParentDO removeWhere(String queryColumn, String queryType) {
        removeWhere(queryColumn, queryType, null);
        return this;
    }

    /**
     * 功能描述: 目前addRestrict后的条件不支持追加查入tosql
     *
     * @author treeyw
     * @date
     */
    public String toSqlWhere() throws Exception {
        return obj2WhereSql(this);
    }

    public ParentDO addRestrict(Predicate res) {
        if (getPredicateList() == null) predicateList = new ArrayList<>();
        predicateList.add(res);
        return this;
    }

    public ParentDO removeRestrict(Predicate res) {
        int rm = -1;
        for (int i = 0; i < predicateList.size(); i++) {
            if (predicateList.get(i).toString() == res.toString()) {
                rm = i;
                break;
            }
        }
        if (rm == -1) return this;
        else predicateList.remove(rm);
        return removeRestrict(res);
    }

    //left查询方法
    public ParentDO left(Object obj, Object tid, Object onid) {

        return this;
    }

    /**
     * 功能描述: 字段改为like查询,执行此操作后，该字段值被置空
     *
     * @author treeyw
     * @date
     */
    public ParentDO colQueryLike(String column) {
        addWhere(column, QueryTypeBO.LIKE, getEntity(this, column));
        setEntity(this, column, null);
        return this;
    }


}
