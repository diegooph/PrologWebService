package test.br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.config.PrologApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {PrologApplication.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {TestContainer.DockerPostgresDatasourceInitializer.class})

public class IntegrationTest {

    private static final JdbcDatabaseContainer<?> container = TestContainer.getContainer();
    
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
