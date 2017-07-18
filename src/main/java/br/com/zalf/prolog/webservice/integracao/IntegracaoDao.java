package br.com.zalf.prolog.webservice.integracao;

import com.sun.istack.internal.NotNull;

/**
 * Created by luiz on 7/17/17.
 */
public interface IntegracaoDao {
    @NotNull
    String getSistemaKey(@NotNull final String userToken);
}