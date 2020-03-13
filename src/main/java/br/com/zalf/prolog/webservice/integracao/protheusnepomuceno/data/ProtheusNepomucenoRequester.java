package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data;

import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.*;
import br.com.zalf.prolog.webservice.integracao.sistema.Requester;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 3/10/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public interface ProtheusNepomucenoRequester extends Requester {
    @NotNull
    List<VeiculoListagemProtheusNepomuceno> getListagemVeiculosUnidadesSelecionadas(
            @NotNull final String codFiliais) throws Throwable;

    @NotNull
    VeiculoAfericaoProtheusNepomuceno getPlacaPneusAfericaoPlaca(@NotNull final String codFilial,
                                                                 @NotNull final String placaVeiculo) throws Throwable;

    @NotNull
    ResponseAfericaoProtheusNepomuceno insertAfericaoPlaca(
            @NotNull final AfericaoPlacaProtheusNepomuceno afericaoPlaca) throws Throwable;

    @NotNull
    List<PneuEstoqueProtheusNepomuceno> getListagemPneusEmEstoque(@NotNull final String codFiliais) throws Throwable;

    @NotNull
    PneuEstoqueProtheusNepomuceno getPneuEmEstoqueAfericaoAvulsa(@NotNull final String codFilial,
                                                                 @NotNull final String codPneu) throws Throwable;

    @NotNull
    ResponseAfericaoProtheusNepomuceno insertAfericaoAvulsa(
            @NotNull final AfericaoAvulsaProtheusNepomuceno afericaoAvulsa) throws Throwable;
}
