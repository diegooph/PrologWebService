package test.br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.config.PrologApplication;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.JdbcDatabaseContainer;

import java.util.Collections;
import java.util.List;

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

    @NotNull
    protected String createPathWithPort(@NotNull final String uri) {
        return "http://localhost:" + port + "/prolog/v2/" + uri;
    }

    @NotNull
    protected TestRestTemplate getTestRestTemplate() {
        final RestTemplateBuilder builder = new RestTemplateBuilder()
                .defaultHeader("Authorization", "Bearer PROLOG_DEV_TEAM")
                .messageConverters(getConverters());
        return new TestRestTemplate(builder);
    }

    @NotNull
    private List<HttpMessageConverter<?>> getConverters() {
        return Collections.singletonList(getGsonHttpConverter());
    }

    @NotNull
    private GsonHttpMessageConverter getGsonHttpConverter() {
        final GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
        gsonHttpMessageConverter.setGson(GsonUtils.getGson());
        gsonHttpMessageConverter.setSupportedMediaTypes(getMediaTypes());
        return gsonHttpMessageConverter;
    }

    @NotNull
    private List<MediaType> getMediaTypes() {
        return Collections.singletonList(MediaType.APPLICATION_JSON);
    }
}
