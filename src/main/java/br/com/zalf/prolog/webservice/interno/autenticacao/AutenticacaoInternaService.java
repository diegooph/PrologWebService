package br.com.zalf.prolog.webservice.interno.autenticacao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.NotAuthorizedException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interno.PrologInternalUser;
import br.com.zalf.prolog.webservice.interno.autenticacao._model.PrologInternalUserAuthentication;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Created on 02/03/20.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class AutenticacaoInternaService {
    @NotNull
    private static final String TAG = AutenticacaoInternaService.class.getSimpleName();
    @NotNull
    private final AutenticacaoInternaDao dao = Injection.provideAutenticacaoLoginSenhaDao();

    void createUsernamePassword(@NotNull final String username,
                                @NotNull final String password) throws ProLogException {
        try {
            dao.createUsernamePassword(username, password);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao enviar dados para o BD", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao verificar dados, tente novamente");
        }
    }

    @NotNull
    @CanIgnoreReturnValue
    public PrologInternalUser login(@NotNull final PrologInternalUserAuthentication userAuthentication) {
        try {
            final PrologInternalUser prologInternalUser = dao
                    .getPrologInternalUser(
                            userAuthentication.getUsername(),
                            // Assim nós mantemos a lógica de geração de token aqui!
                            codUsuarioProlog -> {
                                final String token = UUID.randomUUID().toString();
                                dao.createPrologInternalUserSession(codUsuarioProlog, token);
                                return token;
                            })
                    .orElseThrow(() -> {
                        throw new NotAuthorizedException("Prolog internal user not found with username: "
                                + userAuthentication.getUsername());
                    });
            if (!userAuthentication.doesPasswordMatch(prologInternalUser.getEncryptedPassword())) {
                throw new NotAuthorizedException("Wrong password!");
            }
            return prologInternalUser;
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao enviar dados para o BD", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao verificar dados, tente novamente");
        }
    }
}
