package br.com.zalf.prolog.webservice.integracao.protheusrodalog.data;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import org.jetbrains.annotations.Nullable;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created on 28/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ProtheusRodalogRestClient {
    @Nullable
    private static Retrofit retrofit;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(GsonUtils.getGson()))
                    .baseUrl("http://192.168.221.213:8086/rest/")
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
