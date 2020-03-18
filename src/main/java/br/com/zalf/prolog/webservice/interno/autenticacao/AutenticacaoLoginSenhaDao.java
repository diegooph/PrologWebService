package br.com.zalf.prolog.webservice.interno.autenticacao;

import br.com.zalf.prolog.webservice.interno.autenticacao._model.PrologInternalUser;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 07/11/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface AutenticacaoLoginSenhaDao {

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
     * Método para verificar se usuário e senha existem.
     *
     * @param internalUser Usuário interno do Prolog.
     * @throws Throwable Se algum erro ocorrer.
     */
    void verifyUsernamePassword(@NotNull final PrologInternalUser internalUser) throws Throwable;
}
