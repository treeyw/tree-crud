package io.github.treeyw.crud;

import io.github.treeyw.crud.config.sys.SysConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableTransactionManagement                //事物注解
@EnableCaching
@EnableScheduling
@ServletComponentScan
@EnableConfigurationProperties
@EnableAsync(proxyTargetClass = true)
@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration.class
})
public class MainApplication {


    public static void main(String[] args) throws Exception {
        //免https证书认证FastDfsFileController
        disableSslverifcation();
        //扫描这些包下面的实体，加入持久化管理
        new SpringApplicationBuilder(MainApplication.class)
                .properties("spring.config.location=classpath:/application.yml")
                .run(args);
    }

    public static void disableSslverifcation() throws Exception {
        TrustManager[] trustManagers = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }
        };
        SSLContext sc = SSLContext.getInstance("SSL");
        //       //TODO:取消ssl缓存
        //       sc.getServerSessionContext().setSessionCacheSize(0);
        sc.init(null, trustManagers, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
    }


}
