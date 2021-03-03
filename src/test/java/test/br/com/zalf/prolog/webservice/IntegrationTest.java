package test.br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.config.PrologApplication;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.JdbcDatabaseContainer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {PrologApplication.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan({"br.com.zalf.prolog.webservice", "test.br.com.zalf.prolog.webservice"})
@ContextConfiguration(initializers = {TestContainer.DockerPostgresDatasourceInitializer.class})
@ActiveProfiles(profiles = {"test"})
public class IntegrationTest {

    @Autowired
    @NotNull
    private JdbcDatabaseContainer<?> container;

    @Autowired
    @NotNull
    private Flyway flyway;

    @PostConstruct
    void initialSetup() {
        flyway.migrate();
    }

    @PreDestroy
    void finalSetup() {
        container.stop();
    }
}
