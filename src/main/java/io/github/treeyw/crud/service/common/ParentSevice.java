package io.github.treeyw.crud.service.common;

import com.alibaba.fastjson.JSON;
import io.github.treeyw.crud.config.datasource.model.QueryBO;
import io.github.treeyw.crud.config.datasource.model.QueryTypeBO;
import io.github.treeyw.crud.constant.SqlAttribute;
import io.github.treeyw.crud.dao.jpa.manager.JpaEntityBO;
import io.github.treeyw.crud.util.CheckObjUtil;
import io.github.treeyw.crud.util.ObjectUtil;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.github.treeyw.crud.constant.ClassUtil.checkTransient;
import static io.github.treeyw.crud.constant.SqlAttribute.*;
import static io.github.treeyw.crud.util.ObjectUtil.*;

@Service
public class ParentSevice {

    final static Logger log = LoggerFactory.getLogger(ParentSevice.class);
    //查询调用
    @Autowired
    public ParentQueryService parentQuery;
    //增删改查调用
    @Autowired
    public ParentModifyService parentModify;

    public ParentSevice() {
        super();
    }

    /**
     * @description: 进行排序
     * @author treeyw
     * @date 2024/4/7 17:41
     */
    public static void criteriaAddOrder(CriteriaBuilder cb, CriteriaQuery cq, Root root, Object t) {
        Object sordcloum = getEntity(t, SQL_SORDCLOUM);
        Object sordBy = getEntity(t, SQL_SORD);
        //排序
        if (CheckObjUtil.ckIsNotEmpty(sordcloum, sordBy)) {
            //排序需要的对象
            List<Order> orders = new ArrayList<>();
            //进行排序
            String[] sordCluoms = sordcloum.toString().split(",");
            if (sordBy.toString().equals("desc"))
                Arrays.stream(sordCluoms).forEach(clo -> orders.add(cb.desc(root.get(clo))));
            else
                Arrays.stream(sordCluoms).forEach(clo -> orders.add(cb.asc(root.get(clo))));
            cq.orderBy(orders);
        }
    }

    public static void criteriaAddGroup(CriteriaBuilder cb, CriteriaQuery cq, Root root, Object t) {
        Object groupyStr = getEntity(t, SQL_GROUPBY);
        //排序
        if (CheckObjUtil.ckIsNotEmpty(groupyStr)) {
            //排序需要的对象
            List<Expression<?>> groupByExpressions = new ArrayList<>();
            //进行排序
            String[] columns = groupyStr.toString().split(",");
            Arrays.stream(columns).forEach(clo -> {
                groupByExpressions.add(root.get(clo));
            });
            //设置分组字段
            cq.groupBy(groupByExpressions);
        }
    }

    public static void criteriaObj2Query(JpaEntityBO jb, Object t) {
        if (!CheckObjUtil.ckIsNotEmpty(t)) return;
        List<String> keysList = null;
        if (CheckObjUtil.ckIsNotEmpty(jb.getSelectCloums()))
            keysList = Arrays.asList(jb.getSelectCloums());
        //获取所有值不为空的字段
        List<String> keyList = getEntityFiedNameList(t);
        //如果有先处理res
        if (keyList.contains("predicateList")) {
            for (Predicate res : (List<Predicate>) getEntity(t, "predicateList")) {
                jb.getPredicates().add(res);
            }
        }

        //如果有先处理wherelist
        if (keyList.contains("whereList")) {
            for (QueryBO n : ((List<QueryBO>) getEntity(t, "whereList"))) {
                if (n == null) return;
                //如果指定了keys不为空，又不在key里，停止查询
                if (keysList != null && keysList.contains(n.getQueryColumn())) continue;
                try {
                    if (CheckObjUtil.ckIsNotEmpty(n.getQueryVal())
                            || QueryTypeBO.NOT_NULL.equals(n.getQueryType())
                            || QueryTypeBO.NULL.equals(n.getQueryType())
                    ) {
                        Field f = ObjectUtil.getField(jb.getClazz(), n.getQueryColumn());
                        if (f != null) {
                            //如果查询条件是split_in，值是String需要分割
                            if (QueryTypeBO.SPLIT_IN.equals(n.getQueryType()) && n.getQueryVal() instanceof String) {
                                List<Object> list = new ArrayList<>();
                                for (String s : n.getQueryVal().toString().split(",")) {
                                    list.add(obj2Field(s, f));
                                }
                                n.setQueryVal(list);
                            }
                            //不是in的话整理值
                            else if (!QueryTypeBO.IN.equals(n.getQueryType())) {
                                n.setQueryVal(obj2Field(n.getQueryVal(), f));
                            }
                            criteriaAddQuery(n, jb);
                        }
                    }
                } catch (Exception e) {
                    log.info("自定义条件错误：" + JSON.toJSONString(n) + ":" + e.getMessage(), e);
                }
            }
        }

        //v不为空，且key没有transient注解，以及不是parentDO里transient注解修饰的字段
        for (String k : keyList) {
            //whereList不在此处处理
            if (k.equals("whereList")) return;
            //如果指定了keys不为空，又不在key里，停止查询
            if (keyList != null && !keyList.contains(k)) continue;

            Object v = getEntity(t, k);
            //是否业务字段，业务字段不进行查询
            boolean queryFlag = !checkTransient(jb.getClazz(), k);

            if (CheckObjUtil.ckIsNotEmpty(v) && queryFlag) {
                //delectFlag单独处理
                if (k.equals(SqlAttribute.SQL_DELETEFLAG) && v.toString().equals("0")) {
                    QueryBO queryBO = new QueryBO(k, QueryTypeBO.NE, v);
                    criteriaAddQuery(queryBO, jb);
                } else {
                    QueryBO queryBO = new QueryBO(k, QueryTypeBO.EQUALS, v);
                    criteriaAddQuery(queryBO, jb);
                }
            }
        }

    }

    public static void criteriaAddQuery(QueryBO queryBO, JpaEntityBO jb) {
        criteriaAddQuery(queryBO.getQueryType(), queryBO.getQueryColumn(), queryBO.getQueryVal(), jb);
    }

    /**
     * @description: 追加del的查询条件
     * @author treeyw
     * @date 2024/4/7 17:19
     */
    public static void criteriaAddQueryDelFlag(JpaEntityBO jb, Object t) {
        if (getEntity(t, SQL_DELETEFLAG) == null)
            jb.getPredicates().add(jb.getCb().notEqual(jb.getRoot().get(SQL_DELETEFLAG), 1));
    }

    public static void criteriaAddQuery(String queryType, String key, Object val, JpaEntityBO jb) {
        List<Predicate> predicates = jb.getPredicates();
        CriteriaBuilder query = jb.getCb();
        Root root = jb.getRoot();
        //普通查询
        if (queryType.equals(QueryTypeBO.EQUALS))
            predicates.add(query.equal(root.get(key), val));
            //双向like
        else if (queryType.equals(QueryTypeBO.LIKE)) {
            Expression<String> castedColumn = query.toString(root.get(key));
            predicates.add(query.like(castedColumn, "%" + val + "%"));
        }
        //like前加%
        else if (queryType.equals(QueryTypeBO.LIKE_BEFOR)) {
            Expression<String> castedColumn = query.toString(root.get(key));
            predicates.add(query.like(castedColumn, "%" + val));
        }
        //like后加%
        else if (queryType.equals(QueryTypeBO.LIKE_AFTER)) {
            Expression<String> castedColumn = query.toString(root.get(key));
            predicates.add(query.like(castedColumn, val + "%"));
        }
        //大于
        else if (queryType.equals(QueryTypeBO.GT))
            predicates.add(query.gt(root.get(key), (Number) val));
            //小于
        else if (queryType.equals(QueryTypeBO.LT))
            predicates.add(query.lt(root.get(key), (Number) val));
            //大于等于
        else if (queryType.equals(QueryTypeBO.GE))
            predicates.add(query.ge(root.get(key), (Number) val));
            //小于等于
        else if (queryType.equals(QueryTypeBO.LE))
            predicates.add(query.le(root.get(key), (Number) val));
            //不等于
        else if (queryType.equals(QueryTypeBO.NE))
            predicates.add(query.notEqual(root.get(key), val));
            //为空
        else if (queryType.equals(QueryTypeBO.NULL))
            predicates.add(query.isNull(root.get(key)));
            //不为空
        else if (queryType.equals(QueryTypeBO.NOT_NULL))
            predicates.add(query.isNotNull(root.get(key)));
    }

}
