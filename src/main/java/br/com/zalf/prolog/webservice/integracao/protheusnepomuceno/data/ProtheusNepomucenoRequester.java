package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data;

import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.AfericaoPlacaProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.PneuEstoqueProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.VeiculoAfericaoProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.VeiculoListagemProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.inspecaoremovido.*;
import br.com.zalf.prolog.webservice.integracao.sistema.Requester;
import io.reactivex.rxjava3.core.Observable;
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
            @NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder,
            @NotNull final String codFiliais) throws Throwable;

    @NotNull
    VeiculoAfericaoProtheusNepomuceno getPlacaPneusAfericaoPlaca(
            @NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder,
            @NotNull final String codFilial,
            @NotNull final String placaVeiculo) throws Throwable;

    @NotNull
    ResponseAfericaoProtheusNepomuceno insertAfericaoPlaca(
            @NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder,
            @NotNull final AfericaoPlacaProtheusNepomuceno afericaoPlaca) throws Throwable;

    @NotNull
    List<PneuListagemInspecaoRemovido> getListagemPneusInspecaoRemovido(
            @NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder,
            @NotNull final String codFiliais) throws Throwable;

    @NotNull
    PneuEstoqueProtheusNepomuceno getPneuEmEstoqueAfericaoAvulsa(
            @NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder,
            @NotNull final String codFilial,
            @NotNull final String codPneu) throws Throwable;

    @NotNull
    ResponseAfericaoProtheusNepomuceno insertInspecaoRemovido(
            @NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder,
            @NotNull final InspecaoRemovidoRealizada inspecaoRemovido) throws Throwable;

    @NotNull
    Observable<List<LipPneuProtheusNepomuceno>> getLips(@NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder,
                                                        @NotNull final String codFilial);

    @NotNull
    Observable<List<FilialProtheusNepomuceno>> getFiliais(@NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder,
                                                          @NotNull final String codFilial);

    @NotNull
    Observable<List<CausaSucataPneuProtheusNepomuceno>> getCausasSucata(
            @NotNull final ApiAutenticacaoHolder apiAutenticacaoHolder,
            @NotNull final String codFilial);
}
