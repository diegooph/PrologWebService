package br.com.zalf.prolog.webservice.messaging.push;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;

/**
 * Created on 2021-01-28
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Configuration
public class FirebaseFactory {
    private static final String TAG = FirebaseFactory.class.getSimpleName();
    @NotNull
    private final String databaseUrl;
    @NotNull
    private final String credentials;

    @Autowired
    FirebaseFactory(
            @Value("${firebase.database.url:https://prolog-debug.firebaseio.com}") @NotNull final String databaseUrl,
            @Value("${firebase.credentials:}") @NotNull final String credentials) {
        this.databaseUrl = databaseUrl;
        this.credentials = credentials;
    }

    @PostConstruct
    public void init() {
        if (isNotInitialized()) {
            Log.i(TAG, "Firebase ainda não foi inicializado, iremos tentar inicializar.");
            getCredentials().ifPresent(credentials -> {
                Log.i(TAG, "Inicializando Firebase...");
                FirebaseApp.initializeApp(FirebaseOptions.builder()
                                                  .setCredentials(credentials)
                                                  .setDatabaseUrl(databaseUrl)
                                                  .build());
                Log.i(TAG, "Firebase inicializado com sucesso!!");
            });
        }
    }

    @PreDestroy
    public void finish() {
        if (isInitialized()) {
            FirebaseApp.getInstance().delete();
        }
        Log.i(TAG, "FirebaseApp deletado com sucesso!!");
    }

    @NotNull
    private Optional<GoogleCredentials> getCredentials() {
        if (doWeHaveCredentials()) {
            try (final InputStream io = new FileInputStream(credentials)) {
                return Optional.of(GoogleCredentials.fromStream(io));
            } catch (final FileNotFoundException e) {
                Log.e(TAG, String.format("Arquivo com as credenciais: %s não foi encontrado.", credentials), e);
            } catch (final Throwable e) {
                Log.e(TAG, "Erro ao buscar credenciais", e);
            }
        }
        Log.i(TAG, "Não foram encontrados credenciais, por conta disto o Firebase não será inicializado.");
        return Optional.empty();
    }

    private boolean isInitialized() {
        return !FirebaseApp.getApps().isEmpty();
    }

    private boolean isNotInitialized() {
        return !isInitialized();
    }

    private boolean doWeHaveCredentials() {
        return !StringUtils.isNullOrEmpty(credentials);
    }
}
