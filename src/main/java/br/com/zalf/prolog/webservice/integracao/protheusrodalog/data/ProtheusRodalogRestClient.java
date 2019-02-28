package br.com.zalf.prolog.webservice.integracao.protheusrodalog.data;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import org.jetbrains.annotations.NotNull;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created on 28/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ProtheusRodalogRestClient {
    @NotNull
    private static final Retrofit sRetrofit;

    static {
        // TODO - Setar URL de comunicação (ou buscar no BD)
        sRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.getGson()))
                .baseUrl("")
                .build();
    }

    private ProtheusRodalogRestClient() {
    }

    public static <T> T getService(final Class<T> serviceClass) {
        return sRetrofit.create(serviceClass);
    }
}
