package br.com.zalf.prolog.webservice.implantacao.autenticacao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 07/11/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class ImplantacaoLoginSenhaValidator {
    private static final String TAG = ImplantacaoLoginSenhaValidator.class.getSimpleName();
    private final ImplantacaoLoginSenhaDao dao = Injection.provideImplantacaoLoginSenhaDao();

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