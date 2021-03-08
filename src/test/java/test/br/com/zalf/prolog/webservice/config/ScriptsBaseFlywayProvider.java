package test.br.com.zalf.prolog.webservice.config;

import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;

/**
 * Created on 2021-03-04
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@TestComponent
@Order(1)
public class ScriptsBaseFlywayProvider implements FlywayInstanceProvider {
    @Autowired
    DataSource dataSource;

    @NotNull
    @Override
    public Flyway getFlyway() {
        return Flyway.configure()
                .locations("filesystem:sql/prolog_setup_db/scripts/base")
                .baselineOnMigrate(true)
                .dataSource(dataSource)
                .baselineVersion("0")
                .sqlMigrationPrefix("T")
                .sqlMigrationSeparator("_")
                .load();
    }
}
