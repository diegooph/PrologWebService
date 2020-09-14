package br.com.zalf.prolog.webservice;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 2020-09-14
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Configuration
public class PrologConfig extends ResourceConfig {
    public PrologConfig() {
        packages("jersey.config.server.provider.packages",
                "br.com.zalf.prolog.webservice");
        register(MonitoringResource.class);
        register(MultiPartFeature.class);
    }
}
