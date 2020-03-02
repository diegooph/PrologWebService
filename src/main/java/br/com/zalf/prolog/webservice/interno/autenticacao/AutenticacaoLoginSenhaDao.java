package br.com.zalf.prolog.webservice.interno.autenticacao;

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
     * @param authorization Código de autorização.
     * @throws Throwable Se algum erro ocorrer.
     */
    String verifyUsernamePassword(@NotNull final String authorization) throws Throwable;
}
