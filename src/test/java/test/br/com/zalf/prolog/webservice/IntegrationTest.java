package test.br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.config.PrologApplication;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.Location;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = PrologApplication.class,
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class IntegrationTest {
    @NotNull
    private static final String POSTGRES_IMAGE_WITH_POSTGIS = "postgis/postgis:12-2.5-alpine";
    @Container
    protected static PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>(DockerImageName
                                              .parse(POSTGRES_IMAGE_WITH_POSTGIS)
                                              .asCompatibleSubstituteFor("postgres:12.2"));
    @Autowired
    protected TestRestTemplate restTemplate;

    @BeforeAll
    static void beforeAll() {
        final String setupFilePath =
                Location.FILESYSTEM_PREFIX
                        .concat(new File("sql/prolog_setup_db/scripts/base").getAbsolutePath());
        final String migrationsDoneFilePath =
                Location.FILESYSTEM_PREFIX.concat(new File("sql/migrations/done").getAbsolutePath());
        Flyway.configure()
                .dataSource(postgresContainer.getJdbcUrl(),
                            postgresContainer.getUsername(),
                            postgresContainer.getPassword())
                .sqlMigrationPrefix("")
                .sqlMigrationSeparator("_")
                .locations(setupFilePath, migrationsDoneFilePath)
                .load()
                .migrate();
    }
}
