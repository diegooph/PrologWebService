package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.NotAuthorizedException;
import br.com.zalf.prolog.webservice.interceptors.auth.authenticator.RequestAuthenticator;
import org.jetbrains.annotations.NotNull;


/**
 * Created on 03/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class BaseIntegracaoService implements RequestAuthenticator {
    private static final String TAG = BaseIntegracaoService.class.getSimpleName();

    public void ensureValidToken(@NotNull final String tokenIntegracao) {
        ensureValidToken(tokenIntegracao, TAG);
    }

    public void ensureValidToken(@NotNull final String tokenIntegracao,
                                 @NotNull final String tag) {
        try {
            if (!Injection.provideAutenticacaoIntegracaoDao().verifyIfTokenIntegracaoExists(tokenIntegracao)) {
                throw new NotAuthorizedException("Token Integração não existe no banco de dados: " + tokenIntegracao);
            }
            if (!Injection.provideAutenticacaoIntegracaoDao().verifyIfTokenIsActive(tokenIntegracao)) {
                throw new NotAuthorizedException("O Token de Integração está desativado");
            }
        } catch (final Throwable t) {
            Log.e(tag, String.format("Erro ao verificar se o tokenIntegracao existe: %s", tokenIntegracao), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao verificar Token da Integração");
        }
    }
}