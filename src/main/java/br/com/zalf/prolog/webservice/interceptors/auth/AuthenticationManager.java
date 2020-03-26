package br.com.zalf.prolog.webservice.interceptors.auth;

import br.com.zalf.prolog.webservice.BuildConfig;
import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Cache para os tokens.
 *
 * Como agora juntamente com os tokens também passamos as permissões necessárias para se executar determinado request,
 * essa cache não irá mais atender nossas necessidades. Portanto, @deprecated.
 */
@Deprecated
public class AuthenticationManager {

    private static final TimeUnit EXPIRE_AFTER_ACCESS_TIME_UNITY = BuildConfig.DEBUG ? TimeUnit.SECONDS : TimeUnit.MINUTES;
    private static final AuthenticationManager INSTANCE;

    static {
        INSTANCE = new AuthenticationManager();
    }

    private final LoadingCache<String, Boolean> cache;

    private AuthenticationManager() {
        final AutenticacaoService autenticacaoService = new AutenticacaoService();
        final AuthenticationCacheLoader cacheLoader = new AuthenticationCacheLoader(autenticacaoService);
        cache = CacheBuilder
                .newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(2, EXPIRE_AFTER_ACCESS_TIME_UNITY)
                .build(cacheLoader);
    }

    public static AuthenticationManager getInstance() {
        return INSTANCE;
    }

    public boolean verifyIfTokenExists(final String token) {
        return cache.getUnchecked(token);
    }

    /////////////////////////////////
    //
    // Methods for tests purposes
    //
    ////////////////////////////////
    public boolean isTokenInCache(final String token) {
        return cache.getIfPresent(token) != null;
    }

    public long getCacheSize() {
        return cache.size();
    }

    private static class AuthenticationCacheLoader extends CacheLoader<String, Boolean> {
        private final AutenticacaoService autenticacaoService;

        private AuthenticationCacheLoader(final AutenticacaoService autenticacaoService) {
            this.autenticacaoService = autenticacaoService;
        }

        @Override
        public Boolean load(@NotNull final String token) {
            return true;
//            return autenticacaoService.verifyIfTokenExists(token, true);
        }
    }
}