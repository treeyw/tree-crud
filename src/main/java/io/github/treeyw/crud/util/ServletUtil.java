package io.github.treeyw.crud.util;

import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;


public class ServletUtil {


    public static Boolean sysCountFlag() {
        if (getRequestAttr() == null) return true;
        String countFlag = getRequestAttr().getRequest().getHeader("countFlag");
        return countFlag == null || countFlag.equals("1");
    }

    public static Boolean sysCloumFlag() {
        if (getRequestAttr() == null) return false;
        String countFlag = getRequestAttr().getRequest().getHeader("cloumFlag");
        return "1".equals(countFlag);
    }

    public static ServletRequestAttributes getRequestAttr() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }


    public static HttpServletResponse getResponse() {
        return  getRequestAttr().getResponse();
    }

    public static HttpServletRequest getRequest() {
        return  getRequestAttr().getRequest();
    }

    public static Boolean sysListFlag() {
        if (getRequestAttr() == null) return true;
        String listFlag = getRequestAttr().getRequest().getHeader("listFlag");
        return listFlag == null || listFlag.equals("1");
    }

    //获取当次访问所有参数
    public static Map<String, Object[]> getReuqestMapAndRaw() {
        //声明参数载体map
        Map<String, Object[]> map = new HashMap<>();
        try {
            //获取其raw参数
            String raw = ObjectUtil.inputStream2Str(getRequest().getInputStream());
            Map<String, Object[]> rawmap = new HashMap<>();
            if (CheckObjUtil.ckIsNotEmpty(raw)) JSON.parseObject(raw, HashMap.class).forEach((k, v) -> {
                rawmap.put(k.toString(), new Object[]{v});
            });

            //获取其fromData参数
            if (getRequest().getParameterMap() != null)
                map.putAll(getRequest().getParameterMap());
            if (rawmap != null) map.putAll(rawmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    //获取当次访问的Raw
    public static String getReuqestRaw() {
        try {
            //获取其raw参数
            return ObjectUtil.inputStream2Str(getRequest().getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}
