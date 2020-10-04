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

    /**
     * Método para criar usuário e senha.
     *
     * @param userName login do usuário.
     * @param password senha do usuário.
     * @throws Throwable Se algum erro ocorrer.
     */
    void createUsernamePassword(@NotNull final String userName,
                                @NotNull final String password) throws Throwable;

    /**
     * Método para buscar um usuário interno do Prolog com base no {@code username}.
     * <p>
     * O retorno é um optional pois o usuário pode ou não existir.
     *
     * @param username nome do usuário.
     * @return Um optional que pode ou não conter um usuário logado.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Optional<PrologInternalUser> getPrologInternalUser(@NotNull final String username,
                                                       @NotNull final GetPrologUserToken generateUserToken)
            throws Throwable;

    void createPrologInternalUserSession(@NotNull final Long codUsuarioProlog,
                                         @NotNull final String token);
}
