package br.com.zalf.prolog.webservice.interceptors.auth.authenticator;

import br.com.zalf.prolog.webservice.autenticacao.AutenticacaoService;
import br.com.zalf.prolog.webservice.commons.util.Log;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.NotAuthorizedException;

/**
 * Created on 12/11/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class TokenMarcacaoJornadaAuthenticator extends ProLogAuthenticator {
    private static final String TAG = TokenMarcacaoJornadaAuthenticator.class.getSimpleName();

    TokenMarcacaoJornadaAuthenticator(@NotNull final AutenticacaoService service) {
        super(service);
    }

    @Override
    public void validate(@NotNull final String value,
                         @NotNull final int[] permissions,
                         final boolean needsToHaveAllPermissions,
                         final boolean considerOnlyActiveUsers) throws NotAuthorizedException {
        Log.d(TAG, "Token: " + value);
        if (!service.verifyIfTokenMarcacaoExists(value)) {
            throw new NotAuthorizedException("Token não existe no banco de dados: " + value);
        }
    }
}
