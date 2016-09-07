package br.com.zalf.prolog.webservice.interceptors.auth;

import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by luiz on 07/09/16.
 */
public class AuthenticationManager {
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
                .expireAfterAccess(2, TimeUnit.MINUTES)
                .build(cacheLoader);
    }

    public static AuthenticationManager getInstance() {
        return INSTANCE;
    }

    public boolean validateToken(String token) {
        return cache.getUnchecked(token);
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