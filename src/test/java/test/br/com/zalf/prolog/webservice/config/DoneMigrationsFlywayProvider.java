package test.br.com.zalf.prolog.webservice.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.callback.BaseCallback;
import org.flywaydb.core.api.callback.Callback;
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
@Order(2)
public class DoneMigrationsFlywayProvider implements FlywayInstanceProvider {

    @Autowired
    DataSource dataSource;

    @Override
    public Flyway getFlyway() {
        return Flyway.configure()
                .locations("filesystem:sql/migrations/done")
                .baselineOnMigrate(true)
                .dataSource(dataSource)
                .baselineVersion("0")
                .sqlMigrationPrefix("0")
                .repeatableSqlMigrationPrefix("")
                .sqlMigrationSeparator("_")
                .callbacks(new AfterVersionedCallback())
                .load();
    }
}
