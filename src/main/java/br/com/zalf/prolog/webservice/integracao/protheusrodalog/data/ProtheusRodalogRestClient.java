package br.com.zalf.prolog.webservice.integracao.protheusrodalog.data;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.Nullable;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(new OkHttpClient.Builder()
                            .connectTimeout(DEFAULT_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                            .readTimeout(DEFAULT_TIMEOUT_MINUTES, TimeUnit.MINUTES)
                            .build())
                    .addConverterFactory(GsonConverterFactory.create(GsonUtils.getGson()))
                    .baseUrl("http://131.161.40.131:8086/rest/")
                    .build();
        }
        return retrofit;
    }

    private ProtheusRodalogRestClient() {
    }

    public static <T> T getService(final Class<T> serviceClass) {
        return ProtheusRodalogRestClient.getRetrofit().create(serviceClass);
    }
}
