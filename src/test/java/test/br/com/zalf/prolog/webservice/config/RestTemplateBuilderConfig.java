package test.br.com.zalf.prolog.webservice.config;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

import java.util.Collections;
import java.util.List;

/**
 * Created on 2021-03-03
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@TestConfiguration
public class RestTemplateBuilderConfig {

    private int port;

    @EventListener(WebServerInitializedEvent.class)
    public void onServletContainerInitialized(final WebServerInitializedEvent event) {
        port = event.getWebServer().getPort();
    }

    @Bean
    @NotNull
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder()
                .uriTemplateHandler(getUriTemplateHandler())
                .defaultHeader("Authorization", "Bearer PROLOG_DEV_TEAM")
                .messageConverters(getConverters());
    }

    private UriTemplateHandler getUriTemplateHandler() {
        return new DefaultUriBuilderFactory(generateBasePath());
    }

    private String generateBasePath() {
        return "http://localhost:" + port + "/prolog/v2/";
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