package test.br.com.zalf.prolog.webservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;

import java.util.List;

/**
 * Created on 2021-03-09
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@TestConfiguration
public class FlywayInstanceCallConfig {

    @Autowired
    private List<FlywayInstanceProvider> providers;


    @EventListener(WebServerInitializedEvent.class)
    public void onServletContainerInitialized(final WebServerInitializedEvent event) {
        providers.stream()
                .map(FlywayInstanceProvider::getFlyway)
                .forEach(flyway -> {
                    flyway.repair();
                    flyway.migrate();
                });
    }

}
