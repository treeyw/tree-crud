package io.github.treeyw.crud.service.common;

import io.github.treeyw.crud.util.DateTool;
import io.github.treeyw.crud.util.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

import static io.github.treeyw.crud.util.CheckObjUtil.ckIsNotEmpty;


/**
 * 功能描述: 映射管理MapperXml生成Sql
 *
 * @author treeyw
 * @date
 */
@Service
public class MapperService {
    public static Logger log = LoggerFactory.getLogger(MapperService.class);

    public static String mapperSql(String sql, Object param) {
        Map paramMap = null;
        if (param != null) paramMap = ObjectUtil.obj2Map(param);

        if (paramMap != null) for (Object k : paramMap.keySet()) {
            Object obj = paramMap.get(k);
            if (!ckIsNotEmpty(obj)) continue;
            if (obj instanceof Date)
                sql = sql.replaceAll(("#\\{" + k + "}"), DateTool.dateTimeSec2Str((Date) obj));
            else
                sql = sql.replaceAll(("#\\{" + k + "}"), String.valueOf(obj));
        }

        StringBuilder sqlSb = new StringBuilder();
        for (String s : sql.split("\n")) {
            if (!s.contains("#{")) sqlSb.append(s + "\n");
        }

        if (!sqlSb.toString().toLowerCase().contains(" limit ") && paramMap != null && paramMap.get("page") != null && paramMap.get("size") != null) {
            Integer page = Integer.valueOf(paramMap.get("page").toString());
            Integer size = Integer.valueOf(paramMap.get("size").toString());
            page = (page - 1) * size;
            if (page < 0) page = 0;
            sqlSb.append(" LIMIT " + page + "," + size);
        }

        log.info(sqlSb.toString());
        return sqlSb.toString();
    }

    /**
     * 功能描述: sql处理其中的表达式
     *
     * @author treeyw
     * @date
     */
    public static String sql2Bds(String bdsList, String xsql, Map paramMap) {
        Boolean bdsFlag = true;
        for (String bds : bdsList.split(" and ")) {
            //如果已经是false，可以跳出方法了
            if (!bdsFlag) return "";
            //当前的表达式字符是啥
            String tBdsStr = "";
            //在list内获取
            for (String s : bdsStrList) {
                if (bds.contains(s)) {
                    tBdsStr = s;
                    break;
                }
            }
            //取到表达式前后的值
            int bdsLeft = bds.indexOf(tBdsStr);
            int bdsRight = bdsLeft + tBdsStr.length();
            String bdsBefor = bds.substring(0, bdsLeft).trim();
            String bdsAfter = bds.substring(bdsRight).trim();
            //如果有#就取参数，没有无所谓
            if (bdsBefor.indexOf("#{") > -1) bdsBefor = getParamValueByName(paramMap, bdsBefor);
            if (bdsAfter.indexOf("#{") > -1) bdsAfter = getParamValueByName(paramMap, bdsAfter);
            if (tBdsStr.equals("==")) {
                bdsFlag = bdsBefor.equals(bdsAfter);
            } else if (tBdsStr.equals("!=")) {
                bdsFlag = !bdsBefor.equals(bdsAfter);
            } else if (tBdsStr.equals(">")) {
                bdsFlag = Integer.valueOf(bdsBefor) > Integer.valueOf(bdsAfter);
            } else if (tBdsStr.equals("<")) {
                bdsFlag = Integer.valueOf(bdsBefor) < Integer.valueOf(bdsAfter);
            } else if (tBdsStr.equals("<=")) {
                bdsFlag = Integer.valueOf(bdsBefor) <= Integer.valueOf(bdsAfter);
            } else if (tBdsStr.equals(">=")) {
                bdsFlag = Integer.valueOf(bdsBefor) >= Integer.valueOf(bdsAfter);
            }
        }
        if (bdsFlag) return xsql;
        return "";
    }

    public static List<String> bdsStrList = new ArrayList(
            Arrays.asList((
                    "!=,>=,<=,==,>,<"
            ).split(","))
    );

    //取参数
    public static String getParamValueByName(Map<String, Object> paramMap, String paramName) {
        if (paramMap == null) return null;
        paramName = paramName.replaceAll("\\{", "")
                .replaceAll("}", "")
                .replaceAll("#", "");
        if (ckIsNotEmpty(paramMap.get(paramName)) && ckIsNotEmpty(paramMap.get(paramName))) {
            return String.valueOf(paramMap.get(paramName));
        }
        return null;
    }

    public static String url2countSql(String sql) {
        if (ckIsNotQuerySql(sql)) return "";
        if (sql.toLowerCase().lastIndexOf(" limit ") > -1)
            sql = sql.substring(0, sql.toLowerCase().lastIndexOf(" limit "));
        if (sql.toLowerCase().lastIndexOf(" rn >= ") > -1)
            sql = sql.substring(0, sql.toLowerCase().lastIndexOf(" rn >= ")) + " rn >= 0";
        if (sql.toLowerCase().lastIndexOf(" order by ") > -1)
            sql = sql.substring(0, sql.toLowerCase().lastIndexOf(" order by "));
        //如果不需要groupBy
        if (sql.toLowerCase().indexOf(" group ") == -1) {
            int start = sql.toLowerCase().indexOf("select");
            int end = sql.toLowerCase().lastIndexOf("from");
            sql = sql.replace(sql.substring(start, end), "select count(1) as ascountnum ");
        } else {
            sql = "select count(1) as ascountnum from (" + sql + ") cert_yuansql";
        }
        return sql;
    }

    public static boolean ckIsNotQuerySql(String sql) {
        return (sql.indexOf(" update ") > -1 || sql.indexOf(" insert ") > -1 || sql.indexOf(" delete ") > -1);
    }


}
