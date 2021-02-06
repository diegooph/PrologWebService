package br.com.zalf.prolog.webservice.config;

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
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Created on 2020-09-14
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@SpringBootApplication(scanBasePackages = {"br.com.zalf.prolog.webservice"})
@EntityScan(basePackages = {"br.com.zalf.prolog.webservice"})
@EnableScheduling
public class PrologApplication extends SpringBootServletInitializer {

    public static void main(final String[] args) {
        SpringApplication.run(PrologApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(@NotNull final SpringApplicationBuilder builder) {
        builder.sources(PrologApplication.class);
        return builder;
    }

    @Component
    public static class JerseyConfig extends ResourceConfig {

        @Autowired
        public JerseyConfig(final ObjectMapper objectMapper) {
            this.packages("br.com.zalf.prolog.webservice");
            this.property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
            this.property(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true);
            this.register(MultiPartFeature.class);
            this.register(new ObjectMapperContextResolver(objectMapper));
        }

        @PostConstruct
        public void init() {
            // Register components where DI is needed
            this.register(ApiListingResource.class);
            this.register(SwaggerSerializers.class);
            final BeanConfig swaggerConfigBean = new BeanConfig();
            swaggerConfigBean.setConfigId("Prolog Api Docs");
            swaggerConfigBean.setTitle("Prolog Api Docs");
            swaggerConfigBean.setDescription("Métodos disponíveis para acesso aos dados do Prolog");
            swaggerConfigBean.setVersion("v1");
            swaggerConfigBean.setContact("diogenes@prologapp.com");
            swaggerConfigBean.setSchemes(new String[]{"http", "https"});
            swaggerConfigBean.setHost("localhost:8080");
            swaggerConfigBean.setBasePath("/prolog/v2");
            swaggerConfigBean.setResourcePackage("br.com.zalf.prolog.webservice");
            swaggerConfigBean.setPrettyPrint(true);
            swaggerConfigBean.setScan(true);
        }

        @Provider
        public static class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
            private final ObjectMapper mapper;

            public ObjectMapperContextResolver(final ObjectMapper mapper) {
                this.mapper = mapper;
            }

            @Override
            public ObjectMapper getContext(final Class<?> type) {
                return mapper;
            }
        }
    }
}
