package br.com.zalf.prolog.webservice.integracao.api.pneu;

import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiMarcaBanda;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiMarcaPneu;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiModeloBanda;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiModeloPneu;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 16/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ApiPneuDao {
    @NotNull
    List<ApiMarcaPneu> getMarcasPneu(@NotNull final String tokenIntegracao,
                                     final boolean apenasMarcasPneuAtivas) throws Throwable;

    @NotNull
    List<ApiModeloPneu> getModelosPneu(@NotNull final String tokenIntegracao,
                                       @NotNull final Long codMarcaPneu,
                                       final boolean apenasModelosPneuAtivos) throws Throwable;

    @NotNull
    List<ApiMarcaBanda> getMarcasBanda(@NotNull final String tokenIntegracao,
                                       final boolean apenasMarcasBandaAtivas) throws Throwable;

    @NotNull
    List<ApiModeloBanda> getModelosBanda(@NotNull final String tokenIntegracao,
                                         @NotNull final Long codMarcaBanda,
                                         final boolean apenasModelosBandaAtivos) throws Throwable;
}
