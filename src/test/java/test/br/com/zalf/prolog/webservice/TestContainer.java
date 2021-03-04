package test.br.com.zalf.prolog.webservice;

import org.jetbrains.annotations.NotNull;
import org.junit.ClassRule;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Created on 2021-02-24
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Testcontainers
@TestConfiguration
public class TestContainer {

    private static final String TEST_DB_NAME = "prolog_test";
    private static final String TEST_USERNAME = "prolog_user_test";
    private static final String TEST_PASSWORD = "testaquevai";
    private static final String DOCKER_IMAGE_REPO = "prologapp/postgres-postgis-pg_similarity:latest";
    @ClassRule
    @NotNull
    private static final JdbcDatabaseContainer<?> JDBC_BASE_CONTAINER =
            new PostgreSQLContainer<>(getDockerImageName())
            .withReuse(true)
            .withDatabaseName(TEST_DB_NAME)
            .withUsername(TEST_USERNAME)
            .withPassword(TEST_PASSWORD);

    static {
        JDBC_BASE_CONTAINER.start();
    }

    @NotNull
    private static DockerImageName getDockerImageName() {
        return DockerImageName.parse(DOCKER_IMAGE_REPO)
                .asCompatibleSubstituteFor("postgres");
    }

    @Bean
    @NotNull
    public JdbcDatabaseContainer<?> getContainer() {
        return JDBC_BASE_CONTAINER;
    }

    static class DockerPostgresDatasourceInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(@NotNull final ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    configurableApplicationContext,
                    "spring.datasource.url=" + JDBC_BASE_CONTAINER.getJdbcUrl(),
                    "spring.datasource.username=" + JDBC_BASE_CONTAINER.getUsername(),
                    "spring.datasource.password=" + JDBC_BASE_CONTAINER.getPassword()
            );
        }
    }

}
