package io.github.treeyw.crud.dao.jpa.manager;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;

import java.util.List;

/**
 * @author treeyw
 * @description jpa的实体类查询参数对象，用于封装查询条件、分页信息等
 * @date 2025/8/24 19:43
 */
@Data
public class JpaEntityBO {
    private Class clazz;
    private String dbKey;
    private String tableKey;
    private Integer page = 0;
    private Integer pageSize = 20;
    private String[] selectCloums;
    private CriteriaBuilder cb;
    private List<Predicate> predicates;
    private Root root;

    public Integer getPage() {
        return page;
    }

    public Integer dbPage() {
        int tPage = page;
        tPage = (tPage - 1) * pageSize;
        if (tPage < 0) tPage = 0;
        return tPage;
    }

    public void initCriteria(CriteriaBuilder cbT, List<Predicate> predicateT, Root rootT) {
        cb = cbT;
        predicates = predicateT;
        root = rootT;
    }

    public JpaEntityBO() {
    }

    public JpaEntityBO(Class clazzT, String dbKeyT, String tableKeyT) {
        clazz = clazzT;
        dbKey = dbKeyT;
        tableKey = tableKeyT;
    }

    public JpaEntityBO(Class clazzT, String dbKeyT, String tableKeyT, Object pageT, Object pageSizeT, String... selectCloumsT) {
        clazz = clazzT;
        dbKey = dbKeyT;
        tableKey = tableKeyT;
        if (pageT != null) page = Integer.parseInt(pageT.toString());
        if (pageSizeT != null) pageSize = Integer.parseInt(pageSizeT.toString());
        selectCloums = selectCloumsT;

    }
}
