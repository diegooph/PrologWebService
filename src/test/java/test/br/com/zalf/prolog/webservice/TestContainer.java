package test.br.com.zalf.prolog.webservice;

import org.jetbrains.annotations.NotNull;
import org.junit.ClassRule;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.nio.file.Path;

/**
 * Created on 2021-02-24
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Testcontainers
public class TestContainer {

    private static final String TEST_DB_NAME = "prolog_test";
    private static final String TEST_USERNAME = "prolog_user_test";
    private static final String TEST_PASSWORD = "testaquevai";

    @ClassRule
    private static final JdbcDatabaseContainer<?> JDBC_BASE_CONTAINER =
            new PostgreSQLContainer<>(getDockerImageName())
            .withReuse(true)
            .withDatabaseName(TEST_DB_NAME)
            .withUsername(TEST_USERNAME)
            .withPassword(TEST_PASSWORD);

    static {
        generateDockerFileImage();
        JDBC_BASE_CONTAINER.start();
    }

    public static JdbcDatabaseContainer<?> getContainer() {
        return JDBC_BASE_CONTAINER;
    }

    private static DockerImageName getDockerImageName() {
        return DockerImageName.parse("prolog-test-image:latest")
                .asCompatibleSubstituteFor("postgres");
    }

    private static void generateDockerFileImage() {
       new ImageFromDockerfile("prolog-test-image", false)
                .withDockerfile(getDockerfilePath())
               .get();
    }

    private static Path getDockerfilePath() {
        return new File("./Dockerfile").toPath();
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
