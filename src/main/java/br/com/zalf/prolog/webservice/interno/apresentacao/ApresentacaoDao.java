package br.com.zalf.prolog.webservice.interno.apresentacao;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 13/04/20.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface ApresentacaoDao {

    String getResetaClonaEmpresaApresentacao(@NotNull final String username,
                                             @NotNull final Long codEmpresaBase,
                                             @NotNull final Long codEmpresaUsuario) throws Throwable;
}
