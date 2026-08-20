package io.github.treeyw.crud.config.datasource;

import io.github.treeyw.crud.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static io.github.treeyw.crud.util.CheckObjUtil.ckIsEmpty;

@Slf4j
public class CrudConfig {
    //默认写入修改/创建时间
    public static void modelSaveBefor(Boolean updateFlag, Object t, String userid) {
        Date nowDate = new Date();
        ObjectUtil.setEntityIfAbsent(t, "deleteFlag", 0);
        if (ObjectUtil.getEntity(t, "updateTime") == null || updateFlag) ObjectUtil.setEntity(t, "updateTime", nowDate);
        if (!updateFlag && ckIsEmpty(ObjectUtil.getEntity(t, "createTime"))) {
            ObjectUtil.setEntity(t, "createTime", nowDate);
            ObjectUtil.setEntity(t, "createUserid", userid);
        }
    }

    /**
     * @author treeyw
     * @description 初始化数据库，目前支持 MySQL 和 SQLite
     * @date 2025/8/24 19:43
     */
    public static void createDatabase(String dbType, String dbName, String url, String username, String password) {

        if ("sqlite".equalsIgnoreCase(dbType)) {
            createSqliteDatabase(url);
            return;
        }

        // 提取数据库名称
        if (ckIsEmpty(dbName)) {
            dbName = url.substring(url.lastIndexOf("//") + 2, url.indexOf("?"));
            dbName = dbName.substring(dbName.lastIndexOf("/") + 1);
        }

        // 连接到数据库服务器（不指定数据库）,非第一个//后面的/
        url = stripDatabaseFromUrl(url);

        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {
            //查询是否有这个库了
            if (dbType.equalsIgnoreCase("mysql")) {
                List<String> list = new ArrayList<>();
                try (ResultSet rs = stmt.executeQuery("SELECT schema_name FROM information_schema.schemata  ")) {
                    while (rs.next()) {
                        list.add(rs.getString(1));
                    }
                }
                if (list.contains(dbName)) {
                    log.info("Database " + dbName + " already exists.");
                    return;
                }
                String sql = """
                        CREATE DATABASE IF NOT EXISTS `%s` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
                        """.formatted(dbName);
                stmt.executeUpdate(sql);
                log.info("Database " + dbName + " created (if not exists).");
            }

        } catch (Exception e) {
            log.error("Error creating database: " + e.getMessage(), e);
        }
    }

    /**
     * SQLite 没有独立的建库语句，首次建立连接时会自动创建数据库文件。
     */
    private static void createSqliteDatabase(String url) {
        String sqliteUrl = url.replace("jdbc:log4jdbc:sqlite:", "jdbc:sqlite:");
        try {
            createSqliteParentDirectory(sqliteUrl);
        } catch (Exception e) {
            throw new IllegalStateException("Error creating SQLite database directory: " + sqliteUrl, e);
        }
        try (Connection ignored = DriverManager.getConnection(sqliteUrl)) {
            log.info("SQLite database is ready: {}", sqliteUrl);
        } catch (Exception e) {
            throw new IllegalStateException("Error creating SQLite database: " + sqliteUrl, e);
        }
    }

    private static void createSqliteParentDirectory(String sqliteUrl) throws Exception {
        String location = sqliteUrl.substring("jdbc:sqlite:".length());
        if (location.isBlank() || ":memory:".equals(location) || location.startsWith("file:")) {
            return;
        }
        Path parent = Path.of(location).toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    /**
     * @param url JDBC URL
     * @author treeyw
     * @description 去掉 JDBC URL 中的数据库名，保留参数部分
     * @date 2025/8/24 19:43
     */
    public static String stripDatabaseFromUrl(String url) {
        url=url.replace("log4jdbc:","");
        if (ckIsEmpty(url)) return url;
        if (url.contains("?")) {
            //?前面去掉最后一个/到?中间的
             String temp = url.substring(0, url.indexOf("?"));
             temp= temp.substring(0, temp.lastIndexOf("/"));
                return temp + url.substring(url.indexOf("?"));
            //没参数直接去掉最后一个/到结尾
        }
        return url.substring(0, url.lastIndexOf("/"));
    }

    public static void main(String[] args) {
        System.out.println(stripDatabaseFromUrl("jdbc:log4jdbc:mysql://127.0.0.1:3306/tree?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&rewriteBatchedStatements=true"));




    }
}
