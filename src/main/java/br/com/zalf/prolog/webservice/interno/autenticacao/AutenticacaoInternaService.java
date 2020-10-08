package br.com.zalf.prolog.webservice.interno.autenticacao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.NotAuthorizedException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.interno.PrologInternalUser;
import br.com.zalf.prolog.webservice.interno.autenticacao._model.PrologInternalUserAuthentication;
import br.com.zalf.prolog.webservice.interno.autenticacao._model.PrologInternalUserAuthorization;
import br.com.zalf.prolog.webservice.interno.autenticacao._model.PrologInternalUserAuthorizationFactory;
import br.com.zalf.prolog.webservice.interno.autenticacao._model.PrologInternalUserLogin;
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
    public PrologInternalUser authorize(@NotNull final String headerAuthorization) {
        try {
            final PrologInternalUserAuthorization userAuthorization =
                    PrologInternalUserAuthorizationFactory.fromHeaderAuthorization(headerAuthorization);
            return userAuthorization.authorize(dao);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao autorizar requisição para o header: " + headerAuthorization, e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao validar acesso, tente novamente");
        }
    }

    @NotNull
    @CanIgnoreReturnValue
    public PrologInternalUserLogin login(@NotNull final PrologInternalUserAuthentication userAuthentication) {
        try {
            final PrologInternalUser prologInternalUser = dao
                    .getPrologInternalUserByUsername(userAuthentication.getUsername())
                    .orElseThrow(() -> {
                        throw new NotAuthorizedException("Prolog internal user not found with username: "
                                + userAuthentication.getUsername());
                    });
            if (!BCryptValidator.doesPasswordMatch(
                    userAuthentication.getPassword(),
                    prologInternalUser.getEncryptedPassword())) {
                throw new NotAuthorizedException("Wrong password!");
            }

            final String token = UUID.randomUUID().toString();
            dao.createPrologInternalUserSession(prologInternalUser.getCodigo(), token);

            return PrologInternalUserLogin
                    .builder()
                    .codigo(prologInternalUser.getCodigo())
                    .username(prologInternalUser.getUsername())
                    .databaseUsername(prologInternalUser.getDatabaseUsername())
                    .token(token)
                    .build();
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao enviar dados para o BD", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao verificar dados, tente novamente");
        }
    }
}
