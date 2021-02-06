package br.com.zalf.prolog.webservice.messaging.email.config;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 2021-01-29
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */

@Configuration
public class EmailSenderConfig {
    @NotNull
    private final String apikey;
    @NotNull
    private final String apiSecretKey;

    @Autowired
    public EmailSenderConfig(@Value("${mailjet.api-key:}") @NotNull final String apikey,
                             @Value("${mailjet.api-secret-key:}") @NotNull final String apiSecretKey) {

        this.apikey = apikey;
        this.apiSecretKey = apiSecretKey;
    }

    @Bean
    @NotNull
    public MailjetClient getMailJetClient() {
        return new MailjetClient(this.getOptions());
    }

    @NotNull
    private ClientOptions getOptions() {
        return ClientOptions.builder()
                .apiKey(this.apikey)
                .apiSecretKey(this.apiSecretKey)
                .build();
    }
}
