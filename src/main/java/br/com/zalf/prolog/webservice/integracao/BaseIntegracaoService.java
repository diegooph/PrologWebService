package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.NotAuthorizedException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;


/**
 * Created on 03/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public abstract class BaseIntegracaoService {

    protected void ensureValidToken(@NotNull final String tokenIntegracao,
                                    @NotNull final String tag) throws ProLogException {
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