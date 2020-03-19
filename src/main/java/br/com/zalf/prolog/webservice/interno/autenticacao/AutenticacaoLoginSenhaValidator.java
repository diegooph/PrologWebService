package br.com.zalf.prolog.webservice.interno.autenticacao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.interno.autenticacao._model.PrologInternalUser;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 07/11/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class AutenticacaoLoginSenhaValidator {
    private static final String TAG = AutenticacaoLoginSenhaValidator.class.getSimpleName();
    @NotNull
    private final AutenticacaoLoginSenhaDao dao = Injection.provideAutenticacaoLoginSenhaDao();

    public void verifyUsernamePassword(@NotNull final PrologInternalUser internalUser) {
        try {
            dao.verifyUsernamePassword(internalUser);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao verificar usuário e senha", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro com a conexão");
        }
    }
}