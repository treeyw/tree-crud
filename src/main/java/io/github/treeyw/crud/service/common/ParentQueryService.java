package io.github.treeyw.crud.service.common;

import io.github.treeyw.crud.config.datasource.model.QueryBO;
import io.github.treeyw.crud.config.datasource.model.QueryTypeBO;
import io.github.treeyw.crud.config.model.ListVO;
import io.github.treeyw.crud.dao.jpa.manager.CriteriaManager;
import io.github.treeyw.crud.dao.jpa.manager.JpaEntityBO;
import io.github.treeyw.crud.dao.jpa.manager.JpaManegerBO;
import io.github.treeyw.crud.model.parent.ParentDO;
import io.github.treeyw.crud.util.ObjectUtil;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static io.github.treeyw.crud.constant.ClassUtil.getDbKeyByEntity;
import static io.github.treeyw.crud.constant.ClassUtil.getTableKeyByEntity;
import static io.github.treeyw.crud.constant.SqlAttribute.*;
import static io.github.treeyw.crud.dao.jpa.manager.CriteriaManager.getCriterionClass;
import static io.github.treeyw.crud.service.common.ParentSevice.*;
import static io.github.treeyw.crud.util.ObjectUtil.getEntity;
import static io.github.treeyw.crud.util.ServletUtil.sysCountFlag;
import static io.github.treeyw.crud.util.ServletUtil.sysListFlag;

@Service
@Transactional
public class ParentQueryService {

    @Autowired
    protected CriteriaManager daoCriteria;


    public void joinObjForId(ListVO listVO, String idkey, String objKey, Class clazz, String... select) throws Exception {
        //list无值提前结束方法
        if (listVO == null || listVO.getList() == null || listVO.getList().size() == 0) return;
        Object obj = clazz.newInstance();
        //获取另一张表的数据
        List<QueryBO> where = new ArrayList<>();
        //获取其要查另一张表的id值
        where.add(new QueryBO("id", QueryTypeBO.IN, listVO.idLongs(idkey)));
        ObjectUtil.setEntity(obj, "whereList", where);
        ListVO query = listQuery(obj, false, true, clazz, select);
        if (query != null && query.getList() != null && query.getList().size() > 0) {
            //把另一张表的数据放到listVO中
            listVO.joinObjForId(idkey, objKey, query.getList());
        }
    }

    public void joinObjForKey(ListVO listVO, String idKey, String objIdKey, String objKey, Class clazz, String... select) throws Exception {
        //list无值提前结束方法
        if (listVO == null || listVO.getList() == null || listVO.getList().size() == 0) return;
        Object obj = clazz.newInstance();
        //获取另一张表的数据
        List<QueryBO> where = new ArrayList<>();
        //获取其要查另一张表的id值
        where.add(new QueryBO(objIdKey, QueryTypeBO.IN, listVO.idLongs(idKey)));
        ObjectUtil.setEntity(obj, "whereList", where);
        ListVO query = listQuery(obj, false, true, clazz, select);
        if (query != null && query.getList() != null && query.getList().size() > 0) {
            //把另一张表的数据放到listVO中
            listVO.joinObjForKey(idKey, objIdKey, objKey, query.getList());
        }
    }


    /**
     * @Description //把所有传进来的参数做等于拼接查询
     * @Author treeyw
     * @Date 2020/9/5 10:02
     * @Parameter --
     **/
    public ListVO listQuery(Object t, Boolean countFlag, Boolean listFlag, Class nclazz, String... select) throws Exception {
        ListVO listVo = new ListVO();
        //TODO:groupBys待优化
        Object groupBys = getEntity(t, "groupBys");

        //查询的对象
        JpaEntityBO jpaEntityBO = new JpaEntityBO(nclazz, getDbKeyByEntity(t, nclazz), getTableKeyByEntity(t, nclazz), getEntity(t, SQL_PAGE), getEntity(t, SQL_PAGESIZE), select);

        //需要count
        if (countFlag) {
            listVo.setCount(countByCriteria(t, jpaEntityBO));
            //如果count没有值直接return
            if (listVo.getCount() == 0) return listVo;
        }

        //不需要list就直接返回
        if (!listFlag) return listVo;

        listVo.setList(query(t, jpaEntityBO));
        return listVo;
    }

    public ListVO listQuery(Object t, Class nclass, String... select) {
        try {
            return listQuery(t, sysCountFlag(), sysListFlag(), nclass, select);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new ListVO();
    }


    public ListVO listQuery(Object t, String... select) {
        try {
            return listQuery(t, sysCountFlag(), sysListFlag(), t.getClass(), select);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new ListVO();
    }


    public ListVO listQuery(Object t, Boolean countFlag, Boolean listFlag, String... select) {
        try {
            return listQuery(t, countFlag, listFlag, t.getClass(), select);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new ListVO();
    }

    /**
     * @Description //把所有传进来的参数做等于拼接查询
     * @Author treeyw
     * @Date 2020/9/5 10:02
     * @Parameter --
     **/

    public <T> T getByOnly(Object t, Class nclazz, String... select) {
        try {
            if (listQuery(t, false, true, nclazz, select).getList().size() > 0)
                return (T) listQuery(t, false, true, nclazz, select).getList().get(0);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public <T> T getById(Long id, Class clazz, String... select) {
        try {
            ParentDO pd = new ParentDO();
            pd.setId(id);
            return getByOnly(pd, clazz, select);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public <T> T getByOnly(Object t, String... select) {
        try {
            return getByOnly(t, t.getClass(), select);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * @Description //根据自定义sql进行查询
     * 注：非固定结构的系统表尽量避免使用，维护成本会增加，建议在每个dao层分别实现
     * @Author treeyw
     * @Date 2020/9/10 16:12
     **/

    public List listMapBySql(String sql) {

        return daoCriteria.getEntityManager().createNativeQuery(sql)
                .unwrap(org.hibernate.query.NativeQuery.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .getResultList();
    }

    public List listMapBySql(String sql, Class clazz) {

        return daoCriteria.getEntityManager(clazz).createNativeQuery(sql)
                .unwrap(org.hibernate.query.NativeQuery.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .getResultList();
    }


    public List listBySql(String sql, Class clazz) {
        //TODO: tryCahtch太粗暴，影响效率
        try {
            return daoCriteria.getEntityManager(clazz).createNativeQuery(sql)
                    .unwrap(org.hibernate.query.NativeQuery.class)
                    .setResultTransformer(Transformers.aliasToBean(clazz))
                    .getResultList();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * @Description //根据自定义sql进行查询
     * 注：非固定结构的系统表尽量避免使用，维护成本会增加，建议在每个dao层分别实现
     * @Author treeyw
     * @Date 2020/9/10 16:12
     **/

    public static Class getClassByCriteria(CriteriaQuery<?> query) throws Exception {

        return getCriterionClass(query);
    }

    /**
     * @Description cb是用于查询list的，将cb的查询where全部拿出来，用于count查询
     * @Author treeyw
     * @Date 2024/4/5 2:03
     * @Parameter --
     **/
    public long countByCriteria(Object t, JpaEntityBO jb) {
        try {
            //有groupyBy，停止count
            if (getEntity(t, SQL_GROUPBY) != null) return 1;
            //初始化查询工具
            JpaManegerBO jm = daoCriteria.getEntityManagerSession(jb);
            CriteriaBuilder cb = jm.getManager().getCriteriaBuilder();
            CriteriaQuery<Long> result = cb.createQuery(Long.class);
            Root root = result.from(jb.getClazz());
            List<Predicate> predicates = new ArrayList<>();
            jb.initCriteria(cb, predicates, root);
            //补充查询条件
            criteriaObj2Query(jb, t);
            criteriaAddQueryDelFlag(jb, t);
            //没有groupBy条件，正常count
            result.select(cb.count(root));
            //TODO：追加select字段
            //使用查询条件
            result.where(predicates.toArray(new Predicate[0]));
            //进行count
            return daoCriteria.count(result, jm);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }


    public List<Long> listInId(Object t, JpaEntityBO jb) throws Exception {
        List list = null;
        JpaManegerBO jm = daoCriteria.getEntityManagerSession(jb);
        try {
            //初始化查询工具
            CriteriaBuilder cb = jm.getManager().getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root root = cq.from(jm.getClazz());
            List<Predicate> predicates = new ArrayList<>();
            jb.initCriteria(cb, predicates, root);
            //设置查询条件
            criteriaObj2Query(jb, t);
            criteriaAddQueryDelFlag(jb, t);
            criteriaAddOrder(cb, cq, root, t);
            //使用查询条件
            cq.where(predicates.toArray(new Predicate[0]));
            //只查询ID
            cq.select(root.get("id"));
            TypedQuery<Long> typedQuery = jm.getManager().createQuery(cq);
            typedQuery.setFirstResult(jb.dbPage());
            typedQuery.setMaxResults(jb.getPageSize());
            list = typedQuery.getResultList();
            jm.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (jm != null) jm.close();
        }
        return list;
    }


    // ... rest of the class

    public <T> List query(Object t, JpaEntityBO jb) throws Exception {
        JpaManegerBO jm = daoCriteria.getEntityManagerSession(jb);
        List list = null;
        try {
            CriteriaBuilder cb = jm.getManager().getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(jb.getClazz());
            Root<T> root = cq.from(jb.getClazz());
            List<Predicate> predicates = new ArrayList<>();
            jb.initCriteria(cb, predicates, root);
            boolean pageFlag = true;
            //如果存在groupBy条件，取消id查询
            if (getEntity(t, SQL_GROUPBY) != null) {
                //查询条件
                criteriaObj2Query(jb, t);
                criteriaAddQueryDelFlag(jb, t);
                //设置分组字段
                criteriaAddGroup(cb, cq, root, t);
                cq.where(predicates.toArray(new Predicate[0]));
            }
            //正常idin查询
            else {
                pageFlag = false;
                //查询出来本次查询需要的ID，再进行ID in查询
                List<Long> idList = listInId(t, jb);
                if (idList == null || idList.size() == 0) return new ArrayList();
                //TODO: 查询的字段能使用selections
                cq.where(root.get("id").in(idList));
            }
            //设置排序字段
            criteriaAddOrder(cb, cq, root, t);
            //进行查询
            cq.select(root);
            TypedQuery<T> typedQuery = jm.getManager().createQuery(cq);
            if (pageFlag) {
                typedQuery.setFirstResult(jb.dbPage());
                typedQuery.setMaxResults(jb.getPageSize());
            }
            list = typedQuery.getResultList();
            jm.close();
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (jm != null) jm.close();
            return list;
        }
    }

}
