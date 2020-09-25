package br.com.zalf.prolog.webservice.config;

import br.com.zalf.prolog.webservice.database.DataSourceLifecycleManager;
import br.com.zalf.prolog.webservice.messaging.push.FirebaseLifecycleManager;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContextListener;

/**
 * Created on 2020-09-14
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@SpringBootApplication
public class PrologApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(PrologApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(@NotNull final SpringApplicationBuilder builder) {
        builder.sources(PrologApplication.class);
        return builder;
    }

    @Bean
    public ServletListenerRegistrationBean<ServletContextListener> dataSourceLifecycleManagerRegistrationBean() {
        final ServletListenerRegistrationBean<ServletContextListener> bean = new ServletListenerRegistrationBean<>();
        bean.setListener(new DataSourceLifecycleManager());
        return bean;
    }

    @Bean
    public ServletListenerRegistrationBean<ServletContextListener> firebaseLifecycleManagerRegistrationBean() {
        final ServletListenerRegistrationBean<ServletContextListener> bean = new ServletListenerRegistrationBean<>();
        bean.setListener(new FirebaseLifecycleManager());
        return bean;
    }

    @Bean
    public ServletListenerRegistrationBean<ServletContextListener> prologConsoleTextMakerRegistrationBean() {
        final ServletListenerRegistrationBean<ServletContextListener> bean = new ServletListenerRegistrationBean<>();
        bean.setListener(new ProLogConsoleTextMaker());
        return bean;
    }

    @Configuration
    public static class PrologConfig extends ResourceConfig {
        public PrologConfig() {
            packages(true, "br.com.zalf.prolog.webservice");
            property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
            property(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true);
            register(MultiPartFeature.class);
            register(ProLogApplicationEventListener.class);
        }
    }
}
