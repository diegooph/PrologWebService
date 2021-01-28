package br.com.zalf.prolog.webservice.messaging.push;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    private final String databaseUrl;
    private final String credentials;

    @Autowired
    FirebaseFactory(@Value("${firebase.database.url:https://prolog-debug.firebaseio.com}") final String databaseUrl,
                    @Value("${firebase.credentials:}") final String credentials) {
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
                Log.d(TAG, "Firebase inicializado com sucesso!!");
            });
        }

    }

    @PreDestroy
    public void finish() {
        if (isInitialized()) {
           FirebaseApp.getInstance().delete();
        }
        final String message = "FirebaseApp deletado com sucesso!!";
        Log.d(TAG, message);
    }

    private Optional<GoogleCredentials> getCredentials() {
        if (containsCredentials()) {
            try (final InputStream io = new FileInputStream(credentials)) {
                return Optional.of(GoogleCredentials.fromStream(io));
            } catch (FileNotFoundException e) {
                final String message = String.format("Arquivo com as credenciais: %s não foi encontrado.", credentials);
                Log.e(TAG, message, e);
            } catch (Throwable e) {
                Log.e(TAG, "Erro ao buscar credenciais", e);
            }
        }
        Log.i(TAG, "Não foram encontrados credenciais, por conta disto o Firebase não será inicializado.");
        return Optional.empty();
    }

    private boolean isInitialized() {
        return !FirebaseApp.getApps().isEmpty() && FirebaseApp.getApps().size() != 0;
    }

    private boolean isNotInitialized() {
        return !isInitialized();
    }
    private boolean containsCredentials() {
        return !notContainsCredentials();
    }

    private boolean notContainsCredentials() {
        return StringUtils.isNullOrEmpty(credentials);
    }

}
