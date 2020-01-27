package br.com.zalf.prolog.webservice.messaging;

import br.com.zalf.prolog.webservice.commons.util.Log;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created on 2020-01-27
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FirebaseLifecycleManager implements ServletContextListener {
    private final String TAG = FirebaseLifecycleManager.class.getSimpleName();

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        Log.d(TAG, "FirebaseApp iniciando");
        if (FirebaseApp.getApps().isEmpty()) {
            final FileInputStream serviceAccount;
            try {
                serviceAccount = new FileInputStream("/Users/luiz/Downloads/prolog-debug-firebase-adminsdk.json");
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Erro ao iniciar FirebaseApp", e);
                return;
            }

            try {
                final FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setDatabaseUrl("https://prolog-debug.firebaseio.com")
                        .build();
                FirebaseApp.initializeApp(options);
                Log.d(TAG, "FirebaseApp iniciado com sucesso");
            } catch (IOException e) {
                Log.e(TAG, "Erro ao iniciar FirebaseApp", e);
            }
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
        // Nada acontece se ele já estiver deletado.
        // Veja docs: https://firebase.google.com/docs/reference/admin/java/reference/com/google/firebase/FirebaseApp.html#public-void-delete-
        FirebaseApp.getInstance().delete();
        Log.d(TAG, "FirebaseApp deletado com sucesso");
    }
}
