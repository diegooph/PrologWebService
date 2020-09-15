package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.database.DataSourceLifecycleManager;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContextListener;

/**
 * Created on 2020-09-14
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@SpringBootApplication
public class PrologApplication {
    @Bean
    public ServletListenerRegistrationBean<ServletContextListener> listenerRegistrationBean() {
        final ServletListenerRegistrationBean<ServletContextListener> bean = new ServletListenerRegistrationBean<>();
        bean.setListener(new DataSourceLifecycleManager());
        return bean;

    }

    public static void main(String[] args) {
        SpringApplication.run(PrologApplication.class, args);
    }

    @Configuration
    public static class PrologConfig extends ResourceConfig {
        public PrologConfig() {
            packages("jersey.config.server.provider.packages",
                    "br.com.zalf.prolog.webservice");
            register(MultiPartFeature.class);
        }
    }
}
