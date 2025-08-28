package io.github.treeyw.crud.dao.jpa.comment;

import io.github.treeyw.crud.config.datasource.model.FieldComment;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.util.Iterator;

/**
 * @author treeyw
 * @description jpa建表的时候追加字段长度-默认值-注释等
 * @date 2025/8/24 19:43
 */
@ServletComponentScan
@Configuration
@Slf4j
public class CommentIntegrator implements Integrator {
    public static final CommentIntegrator INSTANCE = new CommentIntegrator();

    public CommentIntegrator() {
        super();
    }

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        processComment(metadata);
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactoryImplementor, SessionFactoryServiceRegistry sessionFactoryServiceRegistry) {
    }

    /**
     * @author treeyw
     * @description 自定义注解加入到建表字段中
     * @date 2025/8/24 19:43
     */
    private void fieldComment2Coumn(FieldComment fieldComment, PersistentClass persistentClass, String columnName) {

        String sqlColumnName = persistentClass.getProperty(columnName).getValue().getColumns().get(0).getText();
        persistentClass.getTable().getColumns().forEach(column -> {
            if (sqlColumnName.equalsIgnoreCase(column.getName())) {
                column.setComment(fieldComment.value());
                //长度
                if (fieldComment.length() != 0)
                    column.setLength(fieldComment.length());
                //默认值
                if (!"".equals(fieldComment.dbDefaultVal()))
                    column.setDefaultValue(fieldComment.dbDefaultVal());
                //字段类型
                if (!"".equals(fieldComment.dbType()))
                    column.setSqlType(fieldComment.dbType());
            }
        });

    }

    private void processComment(Metadata metadata) {
        for (PersistentClass persistentClass : metadata.getEntityBindings()) {

            Property identifierProperty = persistentClass.getIdentifierProperty();
            if (identifierProperty != null) {
                fieldComment(persistentClass, identifierProperty.getName());
            } else {
                org.hibernate.mapping.Component component = persistentClass.getIdentifierMapper();
                if (component != null) {
                    //noinspection unchecked
                    Iterator<Property> iterator = component.getPropertyIterator();
                    while (iterator.hasNext()) {
                        fieldComment(persistentClass, iterator.next().getName());
                    }
                }
            }
            // Process fields with Comment annotation.
            persistentClass.getDeclaredProperties().forEach(property -> fieldComment(persistentClass, property.getName()));

        }
    }

    private void fieldComment(PersistentClass persistentClass, String columnName) {
        Field field = null;
        try {
            field = persistentClass.getMappedClass().getDeclaredField(columnName);
        } catch (NoSuchFieldException | SecurityException ignored) {
            try {
                field = persistentClass.getMappedClass().getSuperclass().getDeclaredField(columnName);
            } catch (NoSuchFieldException | SecurityException ignored2) {
                log.error("实体>" + persistentClass.getMappedClass() + ">字段>" + columnName + ">无法处理");
            }
        }
        //找到字段，并有注解，开始处理
        if (field != null && field.isAnnotationPresent(FieldComment.class))
            fieldComment2Coumn(field.getAnnotation(FieldComment.class), persistentClass, columnName);
    }

}
