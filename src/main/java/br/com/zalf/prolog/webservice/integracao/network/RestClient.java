package br.com.zalf.prolog.webservice.integracao.network;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import org.jetbrains.annotations.NotNull;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RestClient {
    @NotNull
    private static final Retrofit RETROFIT;

    static {
        RETROFIT = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.getGson()))
                .baseUrl("Constants.BASE_URL")
                .client(OkHttp.provideNetworkDefaultClient())
                .build();
    }

    private RestClient() {
    }

    public static <T> T getService(final Class<T> serviceClass) {
        return RETROFIT.create(serviceClass);
    }
}