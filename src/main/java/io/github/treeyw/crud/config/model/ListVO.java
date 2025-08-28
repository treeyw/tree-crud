package io.github.treeyw.crud.config.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static io.github.treeyw.crud.constant.SqlAttribute.PAGESIZE;
import static io.github.treeyw.crud.util.ObjectUtil.getEntity;
import static io.github.treeyw.crud.util.ObjectUtil.setEntity;

@Data
@Slf4j
public class ListVO {
    public ListVO() {
    }

    private Long count = 0L;
    private Long total = 0L;
    private Integer size = 0;
    private Integer current = 0;

    private List list = new ArrayList();
    private Integer exportFlag;

    public Long getTotal() {
        return count;
    }

    public List<Long> idLongs() {
        return idLongs("id");
    }

    public List<String> idStrs() {
        return idStrs("id");
    }

    /**
     * 功能描述: 根据list里的id进行合并list
     *
     * @author treeyw
     * @date
     */
    public void joinObjForId(String idKey, String targetKey, List nList) {
        if (nList == null || nList.size() == 0 || list == null || list.size() == 0) return;
        try {
            Map<String, Object> idObjMap = new HashMap<>();
            nList.forEach(t -> idObjMap.put(getEntity(t, "id").toString(), t));
            for (Object o : list) {
                setEntity(o, targetKey, idObjMap.get(getEntity(o, idKey).toString()));
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void joinObjForKey(String idKey, String objIdKey, String targetKey, List nList) {
        if (nList == null || nList.size() == 0 || list == null || list.size() == 0) return;
        try {
            Map<String, Object> idObjMap = new HashMap<>();
            nList.forEach(t -> idObjMap.put(getEntity(t, objIdKey).toString(), t));
            for (Object o : list) {
                setEntity(o, targetKey, idObjMap.get(getEntity(o, idKey).toString()));
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public List<Long> idLongs(String idKey) {
        if (this.getList() == null || this.getList().size() == 0) return null;
        Set<Long> longs = new HashSet<>();
        this.list.forEach(n -> longs.add(Long.valueOf(getEntity(n, idKey).toString())));
        return new ArrayList(longs);
    }

    public List<String> idStrs(String idKey) {
        if (this.getList() == null || this.getList().size() == 0) return null;
        Set<String> longs = new HashSet<>();
        this.list.forEach(n -> longs.add((String) getEntity(n, idKey)));
        return new ArrayList(longs);
    }

    public List getList() {
        if (list != null) {
        }
        return list;
    }

    //Class c, List<BeginCloumVO> t, Map<String, String> chMap, String[] cloums,
    //Map<String, Map<String, String>> cloumsTypsMap

    public ListVO begin(Class clazz) {
        return this;
    }


}
