package test.br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.config.PrologApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
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
    public static GenericContainer<?> genericContainer =
            new GenericContainer<>(
                    new ImageFromDockerfile()
                            .withDockerfile(new File("Dockerfile").toPath()));
    @Container
    protected static PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>(DockerImageName
                                              .parse(POSTGRES_IMAGE_WITH_POSTGIS)
                                              .asCompatibleSubstituteFor("postgres:12.2"));
    @Autowired
    protected TestRestTemplate restTemplate;

    @BeforeAll
    static void beforeAll() {
        final FlywayConfiguration configuration = new FlywayConfiguration(container.getJdbcUrl(),
                                                                          container.getUsername(),
                                                                          container.getPassword());
        configuration.migrate();
    }
}
