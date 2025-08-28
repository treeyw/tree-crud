package io.github.treeyw.crud.config.init;

import com.alibaba.fastjson.JSONObject;
import io.github.treeyw.crud.config.datasource.model.FieldComment;
import io.github.treeyw.crud.constant.ParameAttribute;
import io.github.treeyw.crud.constant.SqlAttribute;
import io.github.treeyw.crud.service.common.ParentModifyService;
import io.github.treeyw.crud.service.common.ParentQueryService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;

import static io.github.treeyw.crud.config.sys.SysConfig.sysPackagePath;
import static io.github.treeyw.crud.constant.ClassUtil.ST_FIELDCOMMENT;
import static io.github.treeyw.crud.constant.SqlAttribute.*;
import static io.github.treeyw.crud.util.CheckObjUtil.ckIsNotEmpty;

/**
 * @Description //启动服务静态资源配置
 * @Author treeyw
 * @Date 2019/10/31 16:13
 **/
@Component
@Transactional
public class CrashStaticCrud {
    final static Logger log = LoggerFactory.getLogger(CrashStaticCrud.class);

    public static Properties SYS_CONSOLE_YML_PROPERTIES;

    public static void initSysConsoleYml(ConfigurableEnvironment environment) {
        try {
            log.info("加载yml");
            //获取yml
            String ymlclass = environment.getProperty("spring.config.location");
            log.info("environment:" + ymlclass);
            YamlPropertiesFactoryBean yamlMapFactoryBean = new YamlPropertiesFactoryBean();
            if (ckIsNotEmpty(ymlclass))
                yamlMapFactoryBean.setResources(new ClassPathResource(ymlclass));
            else yamlMapFactoryBean.setResources(new ClassPathResource("application.yml"));
            try {
                SYS_CONSOLE_YML_PROPERTIES = yamlMapFactoryBean.getObject();
            } catch (Exception e) {
                try {
                    yamlMapFactoryBean.setResources(new FileSystemResource(ymlclass));
                    SYS_CONSOLE_YML_PROPERTIES = yamlMapFactoryBean.getObject();
                } catch (Exception e2) {
                    yamlMapFactoryBean.setResources(new ClassPathResource("application.yml"));
                    SYS_CONSOLE_YML_PROPERTIES = yamlMapFactoryBean.getObject();
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @PostConstruct
    public void init() throws Exception {
        //装载项目名
        ParameAttribute.PROJECTNAME = SYS_CONSOLE_YML.getProperty("server.servlet.context-path");
        JSONObject.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        log.info("初始化：工程数据源初始化、反射获取表明、列名等数据");
        reflectionStatic();
    }


    public void reflectionStatic() {
        //维护数据源
        Map<String, FieldComment> P_FIELDCOMMENT = new HashMap();
        List<String> P_CLOUM = new ArrayList<>();
        List<String> P_TRANSIENT = new ArrayList<>();
        Map<String, Column> P_COLUMN = new HashMap();
        //反射，维护parentDO里的Transient字段均不进入sql的拼接与生成
        fsParentDO(P_FIELDCOMMENT, P_CLOUM, P_TRANSIENT, P_COLUMN);

        //反射，维护表名和实体名
        new Reflections(new ConfigurationBuilder().forPackages(sysPackagePath)).getTypesAnnotatedWith(Entity.class, true)
                .forEach(n -> {
                    Class className = n;
                    String tableName = className.getName();
                    //如果有table注解，则是jpa的实体类
                    if (n.isAnnotationPresent(Table.class)) {
                        tableName = ((Table) n.getAnnotation(Table.class)).name();
                    }

                    //表名维护
                    SqlAttribute.SQL_CLASSNAME_AND_TABLENAME_MAP.put(className, tableName);
                    SqlAttribute.SQL_TABLENAME_AND_CLASSNAME_MAP.put(tableName, className);
                    //表字段维护
                    CLASS_AND_TRANSIENT.putIfAbsent(className, new ArrayList<>());
                    CLASS_AND_CLOUM.putIfAbsent(className, new ArrayList<>());
                    CLASS_AND_FIELDCOMMENT.putIfAbsent(className, new HashMap<>());
                    CLASS_AND_COLUMN.putIfAbsent(className, new HashMap<>());
                    //初始化
                    CLASS_AND_TRANSIENT.get(className).addAll(P_TRANSIENT);
                    CLASS_AND_CLOUM.get(className).addAll(P_CLOUM);
                    CLASS_AND_FIELDCOMMENT.get(className).putAll(P_FIELDCOMMENT);
                    CLASS_AND_COLUMN.get(className).putAll(P_COLUMN);

                    Field[] fields = n.getDeclaredFields();
                    for (Field field : fields) {
                        //带有业务字段注解的字段
                        if (field.isAnnotationPresent(Transient.class))
                            CLASS_AND_TRANSIENT.get(className).add(field.getName());
                        else
                            CLASS_AND_CLOUM.get(className).add(field.getName());
                        //带有业务注释的字段
                        if (field.isAnnotationPresent(FieldComment.class)) {
                            FieldComment fieldComment = field.getAnnotation(FieldComment.class);
                            CLASS_AND_FIELDCOMMENT.get(className).put(field.getName(), fieldComment);
                            //说如果有分表键
                            if (fieldComment.tableKey()) {
                                SQL_CLASSNAME_AND_TABLEKEY_MAP.put(className, field.getName());
                            }
                            //说如果有分库键
                            if (fieldComment.dbKey()) {
                                SQL_CLASSNAME_AND_DBKEY_MAP.put(className, field.getName());
                            }
                        } else {
                            CLASS_AND_FIELDCOMMENT.get(className).put(field.getName(), ST_FIELDCOMMENT);
                        }
                        //特殊注解
                        if (field.isAnnotationPresent(Column.class))
                            CLASS_AND_COLUMN.get(className).put(field.getName(), field.getAnnotation(Column.class));

                    }
                });
    }

    public static void fsParentDO(Map<String, FieldComment> P_FIELDCOMMENT,
                                  List<String> P_CLOUM,
                                  List<String> P_TRANSIENT, Map<String, Column> P_COLUMN) {
        new Reflections(new ConfigurationBuilder().forPackages(sysPackagePath)).getTypesAnnotatedWith(MappedSuperclass.class, true).forEach(n -> {
            Class className = n;
            CLASS_AND_TRANSIENT.putIfAbsent(className, new ArrayList<>());
            CLASS_AND_CLOUM.putIfAbsent(className, new ArrayList<>());
            CLASS_AND_FIELDCOMMENT.putIfAbsent(className, new HashMap<>());
            Field[] fields = n.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Transient.class)) {
                    CLASS_AND_TRANSIENT.get(className).add(field.getName());
                    P_TRANSIENT.add(field.getName());
                } else {
                    CLASS_AND_CLOUM.get(className).add(field.getName());
                    P_CLOUM.add(field.getName());
                }
                if (field.isAnnotationPresent(FieldComment.class)) {
                    CLASS_AND_FIELDCOMMENT.get(className).put(field.getName(), field.getAnnotation(FieldComment.class));
                    P_FIELDCOMMENT.put(field.getName(), field.getAnnotation(FieldComment.class));
                } else {
                    P_FIELDCOMMENT.put(field.getName(), ST_FIELDCOMMENT);
                }
                //特殊注解
                if (field.isAnnotationPresent(Column.class)) {
                    P_COLUMN.put(field.getName(), field.getAnnotation(Column.class));
                }
            }

        });
    }
    public static class SYS_CONSOLE_YML {
        public static String getProperty(String key) {
            if (SYS_CONSOLE_YML_PROPERTIES == null) return "";
            String v = SYS_CONSOLE_YML_PROPERTIES.getProperty(key);
            if (v == null) return "";
            return v;
        }

        public static Integer getPropertyInt(String key) {
            String v = getProperty(key);
            if (v == "") return 0;
            try {
                return Integer.valueOf(v);
            } catch (Exception e) {
                return 0;
            }
        }
    }


}
