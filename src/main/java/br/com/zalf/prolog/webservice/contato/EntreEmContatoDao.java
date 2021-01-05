package br.com.zalf.prolog.webservice.contato;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 21/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface EntreEmContatoDao {
    @NotNull
    Long insertNovaMensagemContato(@NotNull final MensagemContato contato) throws Throwable;
}