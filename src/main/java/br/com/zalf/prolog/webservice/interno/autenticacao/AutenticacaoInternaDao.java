package br.com.zalf.prolog.webservice.interno.autenticacao;

import br.com.zalf.prolog.webservice.interno.PrologInternalUser;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created on 07/11/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface AutenticacaoInternaDao {

    void createUsernamePassword(@NotNull final String userName,
                                @NotNull final String password) throws Throwable;

    @NotNull
    Optional<PrologInternalUser> getPrologInternalUserByUsername(@NotNull final String username) throws Throwable;

    @NotNull
    Optional<PrologInternalUser> getPrologInternalUserByToken(@NotNull final String token) throws Throwable;

    void createPrologInternalUserSession(@NotNull final Long codUsuarioProlog,
                                         @NotNull final String token);
}
