package br.com.zalf.prolog.webservice.interceptors.auth;

import br.com.zalf.prolog.webservice.BuildConfig;
import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

/**
 * Created by luiz on 07/09/16.
 */
public class AuthenticationManager {
    public static final TimeUnit EXPIRE_AFTER_ACCESS_TIME_UNITY = BuildConfig.DEBUG ? TimeUnit.SECONDS : TimeUnit.MINUTES;
    private static final AuthenticationManager INSTANCE;
    private LoadingCache<String, Boolean> cache;

    static {
        INSTANCE = new AuthenticationManager();
    }

    private AuthenticationManager() {
        AutenticacaoService autenticacaoService = new AutenticacaoService();
        AuthenticationCacheLoader cacheLoader = new AuthenticationCacheLoader(autenticacaoService);
        cache = CacheBuilder
                .newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(2, EXPIRE_AFTER_ACCESS_TIME_UNITY)
                .build(cacheLoader);
    }

    public static AuthenticationManager getInstance() {
        return INSTANCE;
    }

    public boolean verifyIfTokenExists(String token) {
        return cache.getUnchecked(token);
    }

    /////////////////////////////////
    //
    // Methods for tests purposes
    //
    ////////////////////////////////
    public boolean isTokenInCache(String token) {
        return cache.getIfPresent(token) != null;
    }

    public long getCacheSize() {
        return cache.size();
    }

    private static class AuthenticationCacheLoader extends CacheLoader<String, Boolean> {
        private final AutenticacaoService autenticacaoService;

        private AuthenticationCacheLoader(AutenticacaoService autenticacaoService) {
            this.autenticacaoService = autenticacaoService;
        }

        @Override
        public Boolean load(String token) throws Exception {
            return autenticacaoService.verifyIfTokenExists(token);
        }
    }
}