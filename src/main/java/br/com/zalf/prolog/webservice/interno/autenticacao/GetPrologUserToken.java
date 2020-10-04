package br.com.zalf.prolog.webservice.interno.autenticacao;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-10-01
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@FunctionalInterface
public interface GetPrologUserToken {
    @NotNull
    String getPrologUserToken(@NotNull final Long codUsuarioProlog);
}
