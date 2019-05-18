package br.com.zalf.prolog.webservice.integracao.protheusrodalog.data;

import br.com.zalf.prolog.webservice.BuildConfig;
import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created on 28/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ProtheusRodalogRestClient {
    private static final long DEFAULT_TIMEOUT_MINUTES = 1;
    @Nullable
    private static Retrofit retrofit;
    @NotNull
    private static final Map<String, Object> SERVICE_CACHE = new HashMap<>();

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(provideOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create(GsonUtils.getGson()))
                    .baseUrl("http://131.161.40.131:8087/rest/")
                    .build();
        }
        return retrofit;
    }

    private ProtheusRodalogRestClient() {
    }

    @NotNull
    public static <T> T getService(@NotNull final Class<T> serviceClass) {
        final String canonicalName = serviceClass.getCanonicalName();
        if (SERVICE_CACHE.containsKey(canonicalName)) {
            //noinspection SingleStatementInBlock, unchecked
            return (T) SERVICE_CACHE.get(canonicalName);
        }
        final T service = ProtheusRodalogRestClient.getRetrofit().create(serviceClass);
        SERVICE_CACHE.put(serviceClass.getCanonicalName(), service);
        return service;
    }

    @NotNull
    private static OkHttpClient provideOkHttpClient() {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                .readTimeout(DEFAULT_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        if (BuildConfig.DEBUG) {
            final HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
            logger.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.interceptors().add(logger);
        }
        return builder.build();
    }
}
