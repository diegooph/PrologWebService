package br.com.zalf.prolog.webservice.interno.autenticacao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 02/03/20.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class AutenticacaoLoginSenhaService {
    @NotNull
    private static final String TAG = AutenticacaoLoginSenhaService.class.getSimpleName();
    @NotNull
    private final AutenticacaoLoginSenhaDao dao = Injection.provideAutenticacaoLoginSenhaDao();

    void createUsernamePassword(@NotNull final String username,
                                @NotNull final String password) throws ProLogException {
        try {
            dao.createUsernamePassword(username, password);
        } catch (Throwable e) {
            Log.e(TAG, "Erro ao enviar dados para o BD", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao verificar dados, tente novamente");
        }
    }
}
