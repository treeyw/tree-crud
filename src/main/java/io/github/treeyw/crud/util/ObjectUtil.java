package io.github.treeyw.crud.util;

/**
 * @author treeyw
 * @实体类转换为map
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.github.treeyw.crud.constant.SqlAttribute.SQL_NULL;
import static io.github.treeyw.crud.util.CheckObjUtil.ckIsNotEmpty;

@Slf4j
public final class ObjectUtil {

    private ObjectUtil() {
    }


    /**
     * 功能描述: 当前是第几页,从1开始
     *
     * @author treeyw
     * @date
     */
    public static int num2Page(int num, int onePageNum) {
        return ((num / (onePageNum - 1)) + 1);
    }

    /**
     * //获取私有的属性值
     *
     * @Author treeyw
     * @Date 2021/9/1 14:39
     * @Parameter --
     **/
    public static Object getPrivateValue(Object obj, String key) {
        try {
            Field field = obj.getClass().getDeclaredField(key);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

    }

    /**
     * 对象转为map并增加code对应的省市区名称
     * 例如{acode:123} 转为 {acode:123,acodePccName:名称}
     *
     * @Author treeyw
     * @Date 2021/5/13 15:45
     * @Parameter t:传入对象实体，clazz，其泛型，keys需要单位名称的keys
     **/
    public static <T> T entitySetPCCName(T t, Class<T> clazz, String... keys) {
        if (t == null) return t;

        Map map = obj2Map(t);
        for (String key : keys) {
            String code = map.get(key) + "";
            //TODO:!!!
        }
        return map2Obj(map, clazz);
    }

    /**
     * 对象转为map并增加code对应的省市区名称
     * 例如{acode:123} 转为 {acode:123,acodePccName:名称}
     *
     * @Author treeyw
     * @Date 2021/5/13 15:45
     * @Parameter t:传入对象实体，clazz，其泛型，keys需要单位名称的keys
     **/
    public static <T> List<T> listSetPCCName(List<T> list, Class<T> clazz, String... keys) {
        if (list == null)
            return null;
        List<T> newList = new ArrayList();
        list.forEach(n -> newList.add(entitySetPCCName(n, clazz, keys)));
        return newList;
    }

    /**
     * 对象转为map并增加code对应的单位名称
     * 例如{acode:123} 转为 {acode:123,acodePccName:名称}
     *
     * @Author treeyw
     * @Date 2021/5/13 15:45
     * @Parameter t:传入对象实体，clazz，其泛型，keys需要单位名称的keys
     **/
    public static <T> T entitySetUntName(T t, Class<T> clazz, String... keys) {
        if (t == null) return t;

        Map map = obj2Map(t);
        for (String key : keys) {
            String code = map.get(key) + "";
            //TODO:!!!
        }
        return map2Obj(map, clazz);
    }

    /**
     * 对象转为map并增加code对应的单位名称
     * 例如{acode:123} 转为 {acode:123,acodePccName:名称}
     *
     * @Author treeyw
     * @Date 2021/5/13 15:45
     * @Parameter t:传入对象实体，clazz，其泛型，keys需要单位名称的keys
     **/
    public static <T> List<T> listSetUntName(List<T> list, Class<T> clazz, String... keys) {
        if (list == null)
            return null;
        List<T> newList = new ArrayList();
        list.forEach(n -> newList.add(entitySetUntName(n, clazz, keys)));
        return newList;
    }


    //obj根据字段类型进行转义
    public static Object obj2Field(Object ov, Field f) {
        try {
            if (ov == null || f == null) return null;
            //如果目标是时间类型当前是字符串则需要深度强转
            if (f.getType().getSimpleName().equals("Date") && ov instanceof String)
                return f.getType().cast(DateTool.extractDate(ov.toString()));
            else if (f.getType().getSimpleName().equals("Long"))
                return f.getType().cast(Long.valueOf(ov.toString()));
            else if (f.getType().getSimpleName().equals("Integer"))
                return f.getType().cast(Integer.valueOf(ov.toString()));
            return f.getType().cast(ov);
        } catch (Exception e) {
            log.error("错误值:" + ov + ">错误field:" + f);
            throw e;
        }
    }


    /**
     * 修改该list下的某个key为val值
     *
     * @Author treeyw
     * @Date 2020/12/24 11:55
     * @Parameter --
     **/
    public static List listUpdateKeyVal(List list, String key, String val) {
        List newList = new ArrayList();
        list.forEach(item -> {
            Map map = obj2Map(item);
            map.put(key, val);
            newList.add(map);
        });
        return newList;
    }

    /**
     * 用来源去对碰目标，求得比目标多的元素
     *
     * @Author treeyw
     * @Date 2020/12/18 17:27
     * @Parameter --
     **/
    public static List listCompare(List source, List target) {
        if (!ckIsNotEmpty(source)) return new ArrayList();
        if (!ckIsNotEmpty(target)) return source;
        Set<Object> targetSet = new HashSet<>(target);
        Set<Object> sourceSet = new HashSet<>();
        source.forEach(s -> {
            if (targetSet.add(s)) {
                sourceSet.add(s);
            }
        });
        return new ArrayList<>(sourceSet);
    }

    public static List listCompare(Set source, Set target) {
        return listCompare(new ArrayList(source), new ArrayList<>(target));
    }

    public static List listCompare(List source, Set target) {
        return listCompare(source, new ArrayList<>(target));
    }

    public static List listCompare(Set source, List target) {
        return listCompare(new ArrayList(source), target);
    }

    /**
     * 当前实体类赋值1，时间为当前
     *
     * @Author treeyw
     * @Date 2020/11/10 11:48
     * @Parameter --
     **/
    public static <T> T objAnySet(Class clazz) {
        Map nowMap = new HashMap();
        Arrays.stream(clazz.getDeclaredFields())
                .forEach(n -> {
                    n.setAccessible(true);
                    if (n.getType().getSimpleName().equals("Date"))
                        nowMap.put(n.getName(), new Date());
                    else nowMap.put(n.getName(), "1");
                });
        return (T) map2Obj(nowMap, clazz);

    }

    public static List<Object> listObj2in(List list, String key) {
        List listIn = new ArrayList<>();
        list.forEach(d -> {
            listIn.add(obj2Map(d).get(key));
        });
        return listIn;
    }


    /**
     * map里的key全部下划线变驼峰
     *
     * @Author treeyw
     * @Date 2020/3/2 0002 7:52
     * @Parameter --
     **/
    public static void mapKeyLine2Hump(Map map) {
        if (map == null || map.size() == 0) return;
        Set<Object> keySet = new HashSet<>();
        keySet.addAll(map.keySet());
        for (Object key : keySet) {

            //如果没有下划线则跳过
            if (key.toString().indexOf("_") == -1) continue;

            map.put(line2Hump(key.toString()), map.get(key));
            map.remove(key);
        }
    }

    /**
     * map里的key全部下划线变驼峰
     *
     * @param list
     * @Author treeyw
     * @Date 2020/3/2 0002 7:52
     * @Parameter --
     */
    public static void listKeyLine2Hump(List<Map<String, Object>> list) {
        if (list == null) return;
        for (Map m : list) {
            mapKeyLine2Hump(m);
        }
    }

    /**
     * 删除list内某个KEY
     *
     * @Author treeyw
     * @Date 2020/3/2 0002 7:52
     * @Parameter --
     **/
    public static List listRemoveKey(List list, String... keys) {
        if (list == null) return list;
        List dataList = new ArrayList();
        for (Object obj : list) {
            Map map = obj2Map(obj);
            for (String key : keys) {
                if (map.get(key) != null) {
                    map.remove(key);
                }
            }
            dataList.add(map);
        }
        return dataList;
    }

    /**
     * map删除不包含的KEY
     *
     * @Author treeyw
     * @Date 2020/2/25 1:46
     **/
    public static Map delObjNotKey(Object obj, String... notdelKey) {
        Map map = obj2Map(obj);
        Map newMap = new HashMap();
        for (String key : notdelKey) {
            newMap.put(key, map.get(key));
        }
        map = new HashMap();
        map.putAll(newMap);
        return map;
    }

    /**
     * 下划线转驼峰
     */
    public static String line2Hump(String str) {
        if (str.indexOf("_") < 0) return str;

        Matcher matcher = Pattern.compile("_(\\w)").matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 驼峰转下划线
     */
    public static String hump2Line(String str) {
        String yStr = str.substring(0, 1).toLowerCase();
        str = str.substring(1, str.length());
        Matcher matcher = Pattern.compile("[A-Z]").matcher(str);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        String wqTODO = yStr + sb.toString();
        wqTODO = wqTODO.replaceAll("1_", "1")
                .replaceAll("2_", "2")
                .replaceAll("3_", "3")
                .replaceAll("4_", "4")
                .replaceAll("5_", "5")
                .replaceAll("6_", "6")
                .replaceAll("7_", "7")
                .replaceAll("8_", "8")
                .replaceAll("9_", "9")
                .replaceAll("0_", "0");
        return wqTODO;
    }


    /**
     * 有继承类属性转为map
     *
     * @Author treeyw
     * @Date 2018/8/18 16:17
     */
    public static JSONObject obj2JsonObj(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj, SerializerFeature.WriteClassName));
    }

    public static Map<String, Object> obj2Map(Object obj) {
        if (obj == null) return new HashMap<>();

        if (obj instanceof Map) return (Map) obj;

        try {
            Map dataMap = new HashMap();
            if (obj.getClass().getSuperclass() != null && obj.getClass().getSuperclass().getDeclaredFields() != null)
                for (Field field : obj.getClass().getSuperclass().getDeclaredFields()) {
                    field.setAccessible(true);
                    dataMap.put(field.getName(), field.get(obj));
                }
            if (obj.getClass().getDeclaredFields() != null)
                for (Field field : obj.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    dataMap.put(field.getName(), field.get(obj));
                }
            return dataMap;
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        try {
            return JSON.parseObject(JSON.toJSONString(obj, SerializerFeature.WriteClassName));
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();

    }

    public static Map<String, Object> obj2MapNotNull(Object obj) {
        if (obj == null) return new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        try {
            dataMap = (Map) obj;
            return dataMap;
        } catch (Exception e) {
        }
        try {
            dataMap = JSON.parseObject(JSON.toJSONString(obj));
            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                dataMap.put(field.getName(), field.get(obj));
            } catch (Exception e) {
            }
        }
        return dataMap;
    }

    public static <T> T map2Obj(Map map, Class<T> clazz) {
        if (map == null) return null;
        return JSON.parseObject(JSON.toJSONString(map), clazz);
    }

    public static <T> T map2Object(Map<String, Object> map, Class<T> clazz) {
        if (map == null) {
            return null;
        }
        T obj = null;
        try {
            obj = clazz.newInstance();

            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                String filedTypeName = field.getType().getName();
                if (filedTypeName.equalsIgnoreCase("java.util.date")) {
                    String datetimestamp = String.valueOf(map.get(field.getName()));
                    if (datetimestamp.equalsIgnoreCase("null")) {
                        if (obj != null) field.set(obj, null);
                    } else {
                        if (obj != null) field.set(obj, new Date(Long.parseLong(datetimestamp)));
                    }
                } else {
                    if (obj != null) field.set(obj, map.get(field.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * @Author treeyw
     * 多个类合为一个map
     * @Date 2019/3/18 15:28
     * @Param [objs]
     **/
    public static Map<String, Object> objs2Map(Object... objs) {
        if (objs == null) return new HashMap<>();
        Map<String, Object> dataMap = new HashMap<>();
        for (Object obj : objs) {
            dataMap.putAll(obj2Map(obj));
        }
        return dataMap;
    }

    /**
     * @return java.util.List
     * 根据某个key,或者某些Key的总和进行降序,由大到小
     * @Author treeyw
     * @Date 2018/9/17 20:26
     * @接口说明：http://192.168.0.121/web/#
     * @Param [list, key:用于排序的key,desc:desc为降序，其他升序
     */
    public static List sortByKey(List list, Class clazz, String desc, String... key) {
        return JSON.parseArray(JSON.toJSONString(sortByKey(list, desc, key)), clazz);
    }

    public static List sortByKey(List list, String desc, String... key) {
        Map<String, List<Map<String, Object>>> allMap = new HashMap<>();
        List<BigDecimal> decimalsList = new ArrayList<>();
        List<String> notDcmList = new ArrayList<>();

        for (Object obj : list) {
            Map<String, Object> m = obj2Map(obj);
            String keyStr = String.valueOf(m.get(key[0]));
            //如果不是数字、小数类型，就默认未时间，进行墙砖
            try {
                new BigDecimal(keyStr);
                decimalsList.add(new BigDecimal(keyStr));
            } catch (Exception e) {
                try {
                    Date d = (Date) m.get(key[0]);
                    keyStr = d.getTime() + "";
                    decimalsList.add(new BigDecimal(keyStr));
                } catch (Exception e2) {
                    notDcmList.add(keyStr);
                }
            }

            List<Map<String, Object>> dataList = new ArrayList<>();
            if (allMap.get(keyStr) != null) {
                dataList = allMap.get(keyStr);
            }
            dataList.add(m);
            allMap.put(keyStr, dataList);
        }
        //降序
        if (desc.equals("desc")) {
            Collections.sort(decimalsList, new Comparator<BigDecimal>() {
                @Override
                public int compare(BigDecimal o1, BigDecimal o2) {
                    return o2.compareTo(o1);
                }
            });
        }
        //升序
        else {
            Collections.sort(decimalsList);
        }
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (BigDecimal b : decimalsList) {
            String strB = b.toString();
            if (allMap.get(strB) != null) {
                dataList.addAll(allMap.get(strB));
                allMap.remove(strB);
            }
        }
        for (String str : notDcmList) {
            if (allMap.get(str) != null) {
                dataList.addAll(allMap.get(str));
                allMap.remove(str);
            }
        }
        return dataList;
    }

    public static String inputStream2Str(InputStream inputStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

    public static String getRandom(int length) {
        return new Random().ints(0, 10)
                .limit(length)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining());
    }


    //通过身份证获取性别,0女，1男
    public static String getGenderDct(String number) {
        if (!ckIsNotEmpty(number)) return null;
        String sexStr = number.substring(16, 17);
        int i = Integer.parseInt(sexStr);
        if (i % 2 == 0) {
            return "0";
        } else {
            return "1";
        }
    }


    /**
     * 主要用于js端，两个对象进行去重操作，newMap去掉与oldMap相同的数据
     *
     * @Author treeyw
     * @Date 2021/6/10 16:56
     * @Parameter --
     **/
    public static void modelDupRemove(Map oldMap, Map newMap, String... nokeys) {
        if (oldMap == null || newMap == null) return;
        List<String> list = Arrays.asList(nokeys);
        Set<String> keySet = new HashSet<>();
        newMap.keySet().forEach(n -> keySet.add(n.toString()));
        if (keySet != null)
            for (String k : keySet) {
                Object v = newMap.get(k);
                //如果是忽略比较的key则忽略，例如id，old,new都有且相同也不能去掉
                if (list.contains(k)) {
                }
                //当前值为空，老值不为空，则置空
                else if (v == null) {
                    if (oldMap.get(k) != null) {
                        newMap.put(k, SQL_NULL);
                    }
                }
                //list类型
                else if (v instanceof ArrayList) {
                    //如果两者都不为空，逐个下标进行比对
                    if (oldMap.get(k) != null && v != null) {
                        List oldList = (List) oldMap.get(k);
                        List newList = (List) v;
                        List xnewList = new ArrayList();
                        for (int i = 0; i < newList.size(); i++) {
                            Object xobj = newList.get(i);
                            if (xobj instanceof Map) {
                                if (oldList.size() > i)
                                    modelDupRemove((Map) oldList.get(i), (Map) xobj, nokeys);
                            } else {
                                if (oldList.size() > i) {
                                    if (!(xobj + "").equals(oldList.get(i) + "")) {
                                        xnewList.add(xobj);
                                    } else {
                                        xnewList.add(xobj);
                                    }
                                }
                                newList = xnewList;
                            }
                        }
                    }
                }
                //map类型
                else if (v instanceof HashMap) {
                    modelDupRemove((Map) oldMap.get(k), (Map) v, nokeys);
                }
                //单key类型
                else {
                    if (v.toString().equals(oldMap.get(k))) {
                        newMap.remove(k);
                    }
                }
            }
    }

    /**
     * 实体包含的map所有的key平铺到主类里,
     *
     * @Author treeyw
     * @Date 2021/8/12 16:41
     * @Parameter --
     **/
    public static Map obj2MapTile(Object o) {
        Map nMap = new HashMap();
        obj2Map(o).forEach((k, v) -> {
            mapTile(k, v, nMap);
        });
        return nMap;
    }

    /**
     * Map 子集全部给到第一层
     *
     * @Author treeyw
     * @Date 2021/8/12 17:04
     * @Parameter --
     **/
    public static void mapTile(String key, Object obj, Map nMap) {
        if (obj instanceof String
                || obj instanceof Integer
                || obj instanceof Long
                || obj instanceof Date
                || obj instanceof Boolean
                || obj instanceof BigDecimal
                || obj instanceof Double
        )
            nMap.put(key, obj);
        else if (obj instanceof List || obj instanceof ArrayList) {
            obj = list2MapTile((List) obj);
            List<Map> nList = new ArrayList<>();
            ((List<Map>) obj).forEach(v -> {
                Map xmap = new HashMap();
                mapTile(key, v, xmap);
                nList.add(xmap);
            });
            nMap.put(key, nList);
        } else {
            obj2Map(obj).forEach((k, v) -> {
                if (v instanceof String
                        || v instanceof Integer
                        || v instanceof Long
                        || v instanceof Date
                        || v instanceof Boolean
                        || v instanceof BigDecimal
                        || v instanceof Double
                )
                    nMap.put(key + "." + k, v);
                else
                    mapTile(key + "." + k, v, nMap);
            });
        }

    }


    /**
     * list内包含的map平铺到主类里,
     *
     * @Author treeyw
     * @Date 2021/8/12 16:41
     * @Parameter --
     **/
    public static List list2MapTile(List list) {
        List nList = new ArrayList();
        list.forEach(n -> nList.add(obj2MapTile(n)));
        return nList;
    }

    public static void listFlag2Str(List<Object> list) {
        list.forEach(n -> {
            for (Field field : list.getClass().getDeclaredFields()) {
                if (field.getName().indexOf("Flag") > -1) {
                    Object value = getEntity(n, field.getName());
                    if ("0".equals(value))
                        value = "否";
                    else if (("1").equals(value))
                        value = "是";
                    if (value != null) setEntity(n, field.getName(), value);
                }
            }
        });
    }

    /**
     * @Description 如果该字段是空，就给其设置值
     * @Author treeyw
     * @Date 2021-10-30 18:38
     * @Parameter --
     **/
    public static void setEntityIfAbsent(Object t, String key, Object value) {
        if (getEntity(t, key) == null) setEntity(t, key, value);
    }

    /**
     * @Description 实体类设置值
     * @Author treeyw
     * @Date 2021-10-30 18:38
     * @Parameter --
     **/
    public static List<String> MAPCLASS_LIST = new ArrayList(Arrays.asList("HashMap,LinkedHashMap,ConcurrentHashMap".split(",")));

    public static Object[] list2StrArry(List list) {
        if (list == null) return null;
        return list.toArray(new String[list.size()]);
    }

    public static List<String> getEntityFiedNameList(Object t) {
        List<String> keyAll = new ArrayList<>();
        for (Field field : t.getClass().getDeclaredFields()) {
            if (getEntity(t, field.getName()) != null)
                keyAll.add(field.getName());
        }
        for (Field field : t.getClass().getSuperclass().getDeclaredFields()) {
            if (getEntity(t, field.getName()) != null)
                keyAll.add(field.getName());
        }
        return keyAll;
    }

    /**
     * @Description TODO:还可以利用字节码，动态给类追加 switch case ，可以不用反射找到对应的set方法
     * @Author treeyw
     * @Date 2024/4/5 3:01
     * @Parameter --
     **/
    public static void setEntity(Object t, String key, Object value) {
        if (t == null) return;
        if (t instanceof Map) {
            ((Map) t).put(key, value);
            return;
        }
        try {
            String setterMethodName = "set" + Character.toUpperCase(key.charAt(0)) + key.substring(1);
            Method setterMethod = t.getClass().getMethod(setterMethodName, value.getClass());
            setterMethod.invoke(t, value);
        } catch (Exception e) {
            try {
                Field field = t.getClass().getDeclaredField(key);
                field.setAccessible(true);
                field.set(t, value);
            } catch (Exception e2) {
                setEntityParent(t, key, value);
            }
        }
    }

    /**
     * @Description 给其设置值
     * @Author treeyw
     * @Date 2021-10-30 18:38
     * @Parameter --
     **/
    public static void setEntityParent(Object t, String key, Object value) {
        if (t == null) return;
        try {
            Field f = t.getClass().getSuperclass().getDeclaredField(key);
            f.setAccessible(true);
            f.set(t, value);
        } catch (Exception e) {
            //logger.error("对象赋值错误：" + t.getClass().getName() + "." + key + "=" + value);
        }
    }

    /**
     * @Description 获取其值
     * @Author treeyw
     * @Date 2021-10-30 18:38
     * @Parameter --
     **/
    public static Object getEntity(Object t, String key) {
        if (t == null) return null;
        if (t instanceof Map) {
            return ((Map) t).get(key);
        }
        try {
            String getterMethodName = "get" + Character.toUpperCase(key.charAt(0)) + key.substring(1);
            Method getterMethod = t.getClass().getMethod(getterMethodName);
            return getterMethod.invoke(t);
        } catch (Exception e) {
            try {
                Field f = t.getClass().getDeclaredField(key);
                f.setAccessible(true);
                return f.get(t);
            } catch (Exception e2) {
                return getEntityParent(t, key);
            }
        }

    }


    public static Field getField(Class t, String key) {
        if (t == null) return null;
        try {
            return t.getDeclaredField(key);
        } catch (Exception e) {
            try {
                return t.getSuperclass().getDeclaredField(key);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    /**
     * @Description 获取其值
     * @Author treeyw
     * @Date 2021-10-30 18:38
     * @Parameter --
     **/
    public static Object getEntityParent(Object t, String key) {
        if (t == null) return null;
        try {
            Field f = t.getClass().getSuperclass().getDeclaredField(key);
            f.setAccessible(true);
            return f.get(t);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 功能描述: 逆向解开所有树结构，改为同级别的list
     *
     * @author treeyw
     * @date
     */
    public static <T> List recursionListByChil(List<T> chList, String chilKey, List<T> rstList) {
        if (chList == null) return null;
        chList.forEach(n -> {
            rstList.add(n);
            recursionListByChil((List<T>) getEntity(n, chilKey), chilKey, rstList);
        });
        return rstList;
    }

    /**
     * 功能描述: list递归为树结构
     * tkey为当前主键，pkey为子集的父键
     * //调用
     * recursionListByPid(list, "id", "parentCode", null, "childList", rstList);
     *
     * @author treeyw
     * @date
     */
    public static <T> List recursionListByPid(List<T> list, String tKey, String pkey, Object pValue,
                                              String childKey, List<T> rstList) {
        list.stream()
                .filter(n -> (pValue + "").equals(getEntity(n, pkey) + ""))
                .forEach(n -> {
                    rstList.add(n);
                    if (getEntity(n, childKey) == null) {
                        List xRstList = new ArrayList();
                        setEntity(n, childKey, xRstList);
                    }
                    recursionListByPid(list, tKey, pkey, getEntity(n, tKey), childKey, (List<T>) getEntity(n, childKey));
                });
        return rstList;
    }

    /**
     * @description: 返回值没有括号
     * @author treeyw
     * @date 2024/5/17 9:56
     */
    public static String list2sqlIn(Collection collection) {
        StringBuilder sb = new StringBuilder();
        for (Object o : collection) {
            sb.append("'").append(o).append("',");
        }
        return sb.substring(0, sb.length() - 1);
    }
}
