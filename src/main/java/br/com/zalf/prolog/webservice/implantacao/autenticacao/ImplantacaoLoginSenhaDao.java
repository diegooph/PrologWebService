package br.com.zalf.prolog.webservice.implantacao.autenticacao;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 07/11/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface ImplantacaoLoginSenhaDao {

    String verifyUsernamePassword(@NotNull final String usernamePassword) throws Throwable;
}
