package br.com.zalf.prolog.webservice.messaging.email.config;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created on 2021-01-29
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */

@Configuration
public class EmailSenderConfig {

    private final String apikey;
    private final String apiSecretKey;

    @Autowired
    public EmailSenderConfig(@Value("${mailjet.api-key:}") @NotNull final String apikey,
                             @Value("${mailjet.api-secret-key:}") @NotNull final String apiSecretKey) {

        this.apikey = apikey;
        this.apiSecretKey = apiSecretKey;
    }

    @Bean
    public MailjetClient getMailJetClient() {
        final var client = new MailjetClient(this.getOptions());
        return client;
    }

    private ClientOptions getOptions() {
        return ClientOptions.builder()
                .apiKey(this.apikey)
                .apiSecretKey(this.apiSecretKey)
                .build();
    }
}
