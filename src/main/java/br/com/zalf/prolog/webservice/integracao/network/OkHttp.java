package br.com.zalf.prolog.webservice.integracao.network;

import br.com.zalf.prolog.webservice.commons.util.ProLogUtils;
import br.com.zalf.prolog.webservice.log.OkHttpLogInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public final class OkHttp {
    private static final long DEFAULT_TIMEOUT_SECONDS = TimeUnit.MINUTES.toSeconds(3);
    @NotNull
    private static final OkHttpClient DEFAULT_CLIENT;

    static {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(new OkHttpLogInterceptor());

        if (ProLogUtils.isDebug()) {
            // Add logging as last interceptor.
            final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.interceptors().add(logging);
        }

        DEFAULT_CLIENT = builder.build();
    }

    private OkHttp() {
        throw new IllegalStateException(OkHttp.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static OkHttpClient provideNetworkDefaultClient() {
        return DEFAULT_CLIENT;
    }
}