package br.com.zalf.prolog.webservice.implantacao.autenticacao;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 07/11/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface ImplantacaoLoginSenhaDao {

    /**
     * Método para verificar se usuário e senha existem.
     *
     * @param authorization Código de autorização.
     * @throws Throwable Se algum erro ocorrer.
     */
    String verifyUsernamePassword(@NotNull final String authorization) throws Throwable;
}
