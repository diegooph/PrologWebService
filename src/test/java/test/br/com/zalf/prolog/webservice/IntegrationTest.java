package test.br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.config.PrologApplication;
import org.junit.ClassRule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = PrologApplication.class,
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {IntegrationTest.Initializer.class})
public class IntegrationTest {

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
            .withDatabaseName("integration-tests-db")
            .withUsername("sa")
            .withPassword("sa");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(final ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of("spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                                  "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                                  "spring.datasource.password=" + postgreSQLContainer.getPassword())
                    .applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
