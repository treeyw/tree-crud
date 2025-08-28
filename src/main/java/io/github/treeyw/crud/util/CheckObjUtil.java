package io.github.treeyw.crud.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description //校验工具类
 * @Date 2020/9/4 16:31
 * @Parameter --
 **/
@Slf4j
public class CheckObjUtil {
    /**
     * 所有值不为空返回true,有一个值为空或者长度小于1就返回false
     **/
    public static boolean ckIsNotEmpty(Object... objs) {
        if (objs == null) return false;
        if (objs.length == 0) return false;
        for (Object obj : objs) {
            //等于空返回null
            if (obj == null) return false;
                //如果是集合
            else if (obj instanceof List) {
                if (((List) obj).isEmpty()) return false;
            }
            //如果是map
            else if (obj instanceof Map) {
                if (((Map) obj).isEmpty()) return false;
            } else if (obj instanceof Array) {
                if (obj == null || Arrays.asList(obj).size() == 0) return false;
            }
            //普通类型
            else if (obj.toString().length() == 0) return false;
        }
        return true;
    }


    /**
     * 校验金额是否正确
     * @param doub
     * @param min
     * @param max
     * @return
     */
    public static boolean ckDouble(String doub,int min ,int max){
        if(min < 0)
            return false;
        try {
            String reg_money = "\\d+(\\.\\d{"+min+","+max+"})?";// 金额正则,可以没有小数，
            Pattern pattern = Pattern.compile(reg_money);
            Matcher matcher = pattern.matcher(doub);
            boolean ismatch = matcher.matches();
            return ismatch;
        }catch (Exception e){
            log.error("金额格式化错误，输入的参数："+doub+",min:"+min+",max:"+max);
            return false;
        }


    }




    /**
     * @Description //所有值为空返回true,有一个值不为空或者长度大于1就返回false
     **/
    public static boolean ckIsEmpty(Object... objs) {
        if (objs.length == 0) return true;
        for (Object obj : objs) {
            if (ckIsNotEmpty(obj)) return false;
        }
        return true;
    }

    /**
     * @Description //校验都不等于比较字符,有等于的返回false
     **/
    public static boolean cknotEquals(String equse, String... strs) {
        if (strs.length == 0) return false;
        if (equse == null) return true;
        for (String str : strs) {
            if (equse.equals(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @Description 所有值的最小值最大值判断，有一个不不符合则false
     **/
    public static boolean ckLeng(int min, int max, Object... objs) {
        if (objs.length == 0) return false;

        for (Object obj : objs) {
            if (obj == null) return false;
            //如果是集合
            if (obj instanceof List) {
                System.out.println(((List) obj).size() > max);
                System.out.println(((List) obj).size() < min);
                if (((List) obj).size() > max || ((List) obj).size() < min) return false;
            }
            //如果是map
            else if (obj instanceof Map) {
                if (((Map) obj).size() > max || ((Map) obj).size() < min) return false;
            }
            //普通类型
            else if (obj.toString().length() > max || obj.toString().length() < min) return false;
        }
        return true;
    }


    /**
     * @Description //是img 返回 true
     * @Author treeyw
     * @Date 2020/9/28 16:48
     * @Parameter --
     **/
    public static boolean ckImg(File file) {
        try {
            Image image = ImageIO.read(file);
            return image != null;
        } catch (IOException ex) {
            return false;
        }
    }

    public static boolean ckImg(MultipartFile file) {
        try {
            Image image = ImageIO.read(file.getInputStream());
            return image != null;
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * 纯数字正则,是数字返回true
     */
    public static boolean ckNumeric(String str) {
        Pattern pattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");//这个也行
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * @Description //校验当前map 需要的KEY都不为空,通过校验返回null,未通过校验则返回该key
     **/
    public static String ckKeyNotNull(Object obj, String... keys) {
        Map map = ObjectUtil.obj2Map(obj);
        for (String key : keys) {
            if (map.get(key) != null) {
                return key;
            }
        }
        return null;
    }


}
