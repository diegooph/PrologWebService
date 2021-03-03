package test.br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.config.PrologApplication;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.JdbcDatabaseContainer;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {PrologApplication.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {TestContainer.DockerPostgresDatasourceInitializer.class})

public class IntegrationTest {

    @NotNull
    private static final JdbcDatabaseContainer<?> container = TestContainer.getContainer();

    @LocalServerPort
    private int port;

    @BeforeAll
    static void beforeAll() {
        final FlywayConfiguration configuration = new FlywayConfiguration(container.getJdbcUrl(),
                                                                          container.getUsername(),
                                                                          container.getPassword());
        configuration.migrate();
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }
}
