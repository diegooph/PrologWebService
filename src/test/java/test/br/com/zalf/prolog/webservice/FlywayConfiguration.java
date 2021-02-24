package test.br.com.zalf.prolog.webservice;

import lombok.Value;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.Location;

import java.io.File;

/**
 * Created on 2021-02-24
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value
public class FlywayConfiguration {

    private static final String TEST_MIGRATION_PREFIX = "T";
    private static final String TEST_MIGRATION_SEPARATOR = "_";
    private static final String BASELINE_VERSION = "0";
    String jdbcUrl;
    String username;
    String password;

    private static String getSetupMigrationsPath() {
        final String setupMigrationsAbsolutePath = new File("sql/prolog_setup_db/scripts/base").getAbsolutePath();
        return Location.FILESYSTEM_PREFIX + setupMigrationsAbsolutePath;
    }

    private static String getDoneMigrationsPath() {
        final String doneMigrationsAbsolutePath = new File("sql/migrations/done").getAbsolutePath();
        return Location.FILESYSTEM_PREFIX + doneMigrationsAbsolutePath;
    }

    public void migrate() {
        getFlywayConfig()
                .migrate();
    }

    private Flyway getFlywayConfig() {
      return Flyway.configure()
                .baselineOnMigrate(true)
                .baselineVersion(BASELINE_VERSION)
                .sqlMigrationPrefix(TEST_MIGRATION_PREFIX)
                .sqlMigrationSeparator(TEST_MIGRATION_SEPARATOR)
                .locations(getDoneMigrationsPath(),
                           getSetupMigrationsPath())
                .dataSource(this.jdbcUrl, this.username, this.password)
                .load();
    }
}
