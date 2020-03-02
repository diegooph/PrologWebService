package br.com.zalf.prolog.webservice.interno.autenticacao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 07/11/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class AutenticacaoLoginSenhaValidator {
    private static final String TAG = AutenticacaoLoginSenhaValidator.class.getSimpleName();
    private final AutenticacaoLoginSenhaDao dao = Injection.provideAutenticacaoLoginSenhaDao();

    public String verifyUsernamePassword(@NotNull final String authorization) {
        try {
            return dao.verifyUsernamePassword(authorization);
        } catch (Throwable e) {
            Log.e(TAG, String.format("Erro ao verificar usuário e senha"), e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro com a conexão");
        }
    }
}