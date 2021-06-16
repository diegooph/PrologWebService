package br.com.zalf.prolog.webservice.integracao.network;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import org.jetbrains.annotations.NotNull;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class RestClient {
    @NotNull
    private static final String DEFAULT_BASE_URL = "http://localhost:8080/";
    @NotNull
    private static final ConcurrentMap<String, Object> SERVICE_CACHE = new ConcurrentHashMap<>();
    @NotNull
    private static final Retrofit RETROFIT;

    static {
        RETROFIT = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GsonUtils.getGson()))
                .client(OkHttp.provideNetworkDefaultClient())
                .baseUrl(DEFAULT_BASE_URL)
                .build();
    }

    private RestClient() {
    }

    @NotNull
    public static <T> T getService(@NotNull final Class<T> serviceClass) {
        final String canonicalName = serviceClass.getCanonicalName();
        //noinspection unchecked
        return (T) SERVICE_CACHE.computeIfAbsent(canonicalName, key -> RETROFIT.create(serviceClass));
    }
}