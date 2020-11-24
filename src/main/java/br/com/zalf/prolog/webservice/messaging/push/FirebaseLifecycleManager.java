package br.com.zalf.prolog.webservice.messaging.push;

import br.com.zalf.prolog.webservice.commons.util.EnvironmentHelper;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogUtils;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created on 2020-01-27
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FirebaseLifecycleManager implements ServletContextListener {
    private static final String DATABASE_URL_DEBUG = "https://prolog-debug.firebaseio.com";
    private static final String DATABASE_URL_PROD = "https://prolog-prod.firebaseio.com";
    private static final String DATABASE_URL = ProLogUtils.isDebug() ? DATABASE_URL_DEBUG : DATABASE_URL_PROD;
    private static final String TAG = FirebaseLifecycleManager.class.getSimpleName();

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        // Estando em debug, não faz mal se não tivermos as credenciais, apenas ignoramos a inicialização do app do
        // Firebase.
        if (ProLogUtils.isDebug() && StringUtils.isNullOrEmpty(EnvironmentHelper.GOOGLE_APPLICATION_CREDENTIALS)) {
            Log.d(TAG, "Sem credenciais do google para iniciar o firebase!");
            return;
        }

        if (FirebaseApp.getApps().isEmpty()) {
            final FileInputStream inputStream;
            try {
                inputStream = new FileInputStream(EnvironmentHelper.GOOGLE_APPLICATION_CREDENTIALS);
            } catch (final Throwable t) {
                // Em debug damos apenas um warning, pois não será necessário ter o firebase rodando para a maioria dos
                // casos.
                if (ProLogUtils.isDebug()) {
                    Log.w(TAG, "Erro ao iniciar firebase!");
                } else {
                    Log.e(TAG, "Erro ao iniciar FirebaseApp", t);
                }
                return;
            }

            try {
                final FirebaseOptions options = new FirebaseOptions.Builder()
                        // See: https://firebase.google.com/docs/admin/setup#initialize-sdk
                        .setCredentials(GoogleCredentials.fromStream(inputStream))
                        .setDatabaseUrl(DATABASE_URL)
                        .build();
                FirebaseApp.initializeApp(options);
                Log.d(TAG, "FirebaseApp iniciado com sucesso");
            } catch (final IOException e) {
                Log.e(TAG, "Erro ao iniciar FirebaseApp", e);
            }
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
        // Nada acontece se ele já estiver deletado.
        // Veja docs: https://firebase.google.com/docs/reference/admin/java/reference/com/google/firebase/FirebaseApp.html#public-void-delete-
        if (!FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.getInstance().delete();
        }
        Log.d(TAG, "FirebaseApp deletado com sucesso");
    }
}
