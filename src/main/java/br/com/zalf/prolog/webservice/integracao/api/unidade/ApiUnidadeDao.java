package br.com.zalf.prolog.webservice.integracao.api.unidade;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 18/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ApiUnidadeDao {
    @NotNull
    List<ApiUnidade> getUnidades(@NotNull final String tokenIntegracao,
                                 final boolean apenasUnidadesAtivas) throws Throwable;
}
