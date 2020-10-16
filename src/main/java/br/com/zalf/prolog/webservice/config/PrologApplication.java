package br.com.zalf.prolog.webservice.config;

import br.com.zalf.prolog.webservice.database.DataSourceLifecycleManager;
import br.com.zalf.prolog.webservice.messaging.push.FirebaseLifecycleManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContextListener;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

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

    @Component
    @ApplicationPath("/prolog/v2")
    public static class JerseyConfig extends ResourceConfig {

        @Autowired
        public JerseyConfig(ObjectMapper objectMapper) {
            this.packages("br.com.zalf.prolog.webservice.geral.unidade");
            this.property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
            this.property(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true);
            this.register(MultiPartFeature.class);
            this.register(ProLogApplicationEventListener.class);
            this.register(new ObjectMapperContextResolver(objectMapper));
        }

        @Provider
        public static class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
            private final ObjectMapper mapper;

            public ObjectMapperContextResolver(ObjectMapper mapper) {
                this.mapper = mapper;
            }

            @Override
            public ObjectMapper getContext(Class<?> type) {
                return mapper;
            }
        }

        @PostConstruct
        public void init() {
            // Register components where DI is needed
            this.register(ApiListingResource.class);
            this.register(SwaggerSerializers.class);

            final BeanConfig swaggerConfigBean = new BeanConfig();
            swaggerConfigBean.setConfigId("Prolog Api");
            swaggerConfigBean.setTitle("Prolog Api");
            swaggerConfigBean.setDescription("Métodos disponíveis para acesso aos dados do Prolog");
            swaggerConfigBean.setVersion("v1");
            swaggerConfigBean.setContact("diogenes@prologapp.com");
            swaggerConfigBean.setSchemes(new String[]{"http", "https"});
            swaggerConfigBean.setHost("localhost:8080");
            swaggerConfigBean.setBasePath("/prolog/v2");
            swaggerConfigBean.setResourcePackage("br.com.zalf.prolog.webservice.geral.unidade");
            swaggerConfigBean.setPrettyPrint(true);
            swaggerConfigBean.setScan(true);
        }
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

    @Override
    protected SpringApplicationBuilder configure(@NotNull final SpringApplicationBuilder builder) {
        builder.sources(PrologApplication.class);
        return builder;
    }
}
