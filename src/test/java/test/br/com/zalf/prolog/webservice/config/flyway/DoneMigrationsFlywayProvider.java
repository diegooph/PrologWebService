package test.br.com.zalf.prolog.webservice.config.flyway;

import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.core.annotation.Order;
import test.br.com.zalf.prolog.webservice.config.LiberaTodasPermissoesCallback;

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

    @NotNull
    @Override
    public Flyway getFlyway() {
        final Flyway load = Flyway.configure()
                .locations("filesystem:sql/migrations/done")
                .baselineOnMigrate(true)
                .dataSource(dataSource)
                .baselineVersion("0")
                .sqlMigrationPrefix("0")
                .repeatableSqlMigrationPrefix("")
                .sqlMigrationSeparator("_")
                .table("migrations_done")
                .callbacks(new LiberaTodasPermissoesCallback())
                .load();
        return load;
    }
}
