package io.github.treeyw.crud.dao.jpa.filter;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.hibernate.EmptyInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author treeyw
 * @description 目前用于分表键，替换目标表名
 * @date 2025/8/24 19:43
 */
@Slf4j
@Component
@Transactional
public class DaoFiltel extends EmptyInterceptor {
    private String srcName = StringUtils.EMPTY; //源表名
    private String destName = StringUtils.EMPTY; // 目标表名

    public DaoFiltel() {
    }

    public DaoFiltel(String srcName, String destName) {
        this.srcName = srcName;
        this.destName = destName;
    }
    //利用拦截器替换表名


    public String onPrepareStatement(String sql, Object[] params) {
        if (srcName.equals(StringUtils.EMPTY) || destName.equals(StringUtils.EMPTY)) {
            return sql;
        }
        sql = sql.replaceAll(srcName, destName);
        return sql;
    }
}
