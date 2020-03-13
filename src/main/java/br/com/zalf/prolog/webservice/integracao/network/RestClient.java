package br.com.zalf.prolog.webservice.integracao.network;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import org.jetbrains.annotations.NotNull;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.HashMap;
import java.util.Map;

public final class RestClient {
    @NotNull
    private static final String DEFAULT_BASE_URL = "http://localhost:8080/";
    @NotNull
    private static final Map<String, Object> SERVICE_CACHE = new HashMap<>();
    @NotNull
    private static final Retrofit RETROFIT;

    static {
        RETROFIT = new Retrofit.Builder()
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
        if (!SERVICE_CACHE.containsKey(canonicalName)) {
            synchronized (RestClient.class) {
                if (!SERVICE_CACHE.containsKey(canonicalName)) {
                    final T service = RETROFIT.create(serviceClass);
                    SERVICE_CACHE.put(serviceClass.getCanonicalName(), service);
                }
            }
        }
        //noinspection unchecked
        return (T) SERVICE_CACHE.get(canonicalName);
    }
}