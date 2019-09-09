package br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo;

import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiMarcaBanda;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiMarcaPneu;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiModeloBanda;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiModeloPneu;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 21/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ApiMarcaModeloPneuDao {
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
