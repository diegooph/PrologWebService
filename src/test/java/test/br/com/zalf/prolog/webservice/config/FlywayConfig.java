package test.br.com.zalf.prolog.webservice.config;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Order;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Created on 2021-03-17
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Component
@ConditionalOnMissingBean(Flyway.class)
public class FlywayConfig {

    @Bean
    public FlywayMigrationStrategy strategy() {
        return strategy -> {
            strategy.repair();
            strategy.migrate();
        };
    }


    @Bean
    @Order(1)
    public FlywayConfigurationCustomizer customizer2() {
        return configuration -> {
            configuration.baselineOnMigrate(true)
                    .baselineVersion("0")
                    .sqlMigrationPrefix("T")
                    .sqlMigrationSeparator("_");
        };
    }

    @Bean
    @Order(2)
    public FlywayConfigurationCustomizer customizer() {
        return configuration -> {
            configuration
                         .baselineOnMigrate(true)
                         .baselineVersion("0")
                         .sqlMigrationPrefix("0")
                         .repeatableSqlMigrationPrefix("")
                         .sqlMigrationSeparator("_");
        };
    }
}
