package br.com.zalf.prolog.webservice.interno.apresentacao;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 13/04/20.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface ApresentacaoDao {

    @NotNull
    String resetaEmpresaApresentacaoUsuario(@NotNull final Long codUsuario,
                                            @NotNull final Long codEmpresaBase,
                                            @NotNull final Long codEmpresaUsuario) throws Throwable;
}
