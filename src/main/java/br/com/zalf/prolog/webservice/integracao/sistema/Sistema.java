package br.com.zalf.prolog.webservice.integracao.sistema;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistSelecao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.operacoes.OperacoesIntegradas;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by luiz on 7/17/17.
 */
public abstract class Sistema implements OperacoesIntegradas {
    @NotNull
    private final IntegradorProLog integradorProLog;
    @NotNull
    private final SistemaKey sistemaKey;
    @NotNull
    private final String userToken;

    protected Sistema(IntegradorProLog integradorProLog, SistemaKey sistemaKey, String userToken) {
        this.integradorProLog = checkNotNull(integradorProLog, "integradorProLog não pode ser nulo!");
        this.sistemaKey = checkNotNull(sistemaKey, "sistemaKey não pode ser nulo!");
        this.userToken = checkNotNull(userToken, "userToken não pode ser nulo!");
    }

    @NotNull
    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull Long codUnidade, @Nullable Boolean ativos) throws
            Exception {
        return getIntegradorProLog().getVeiculosAtivosByUnidade(codUnidade, ativos);
    }

    @NotNull
    @Override
    public List<TipoVeiculo> getTiposVeiculosByEmpresa(@NotNull Long codEmpresa) throws Throwable {
        return getIntegradorProLog().getTiposVeiculosByEmpresa(codEmpresa);
    }

    @NotNull
    @Override
    public List<String> getPlacasVeiculosByTipo(@NotNull Long codUnidade, @NotNull String codTipo) throws Exception {
        return getIntegradorProLog().getPlacasVeiculosByTipo(codUnidade, codTipo);
    }

    @NotNull
    @Override
    public List<ModeloChecklistSelecao> getModelosSelecaoRealizacao(@NotNull final Long codUnidade,
                                                                    @NotNull final Long codCargo) throws Throwable {
        return getIntegradorProLog().getModelosSelecaoRealizacao(codUnidade, codCargo);
    }

    @NotNull
    @Override
    public Veiculo getVeiculoByPlaca(@NotNull String placa, boolean withPneus) throws Exception {
        return getIntegradorProLog().getVeiculoByPlaca(placa, withPneus);
    }

    @NotNull
    @Override
    public CronogramaAfericao getCronogramaAfericao(@NotNull final Long codUnidade) throws Throwable {
        return getIntegradorProLog().getCronogramaAfericao(codUnidade);
    }

    @NotNull
    @Override
    public NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull Long codUnidade,
                                                  @NotNull String placaVeiculo,
                                                  @NotNull String tipoAfericao) throws Throwable {
        return getIntegradorProLog().getNovaAfericaoPlaca(codUnidade, placaVeiculo, tipoAfericao);
    }

    @NotNull
    @Override
    public List<PneuAfericaoAvulsa> getPneusAfericaoAvulsa(@NotNull final Long codUnidade) throws Throwable {
        return getIntegradorProLog().getPneusAfericaoAvulsa(codUnidade);
    }

    @NotNull
    @Override
    public Report getAfericoesAvulsas(@NotNull final Long codUnidade,
                                      @Nullable final Long codColaborador,
                                      @NotNull final LocalDate dataInicial,
                                      @NotNull final LocalDate dataFinal) throws Throwable {
        return getIntegradorProLog().getAfericoesAvulsas(codUnidade, codColaborador, dataInicial, dataFinal);
    }

    @Nullable
    @Override
    public Long insertAfericao(@NotNull final Long codUnidade, @NotNull final Afericao afericao) throws Throwable {
        return getIntegradorProLog().insertAfericao(codUnidade, afericao);
    }

    @NotNull
    @Override
    public Afericao getAfericaoByCodigo(@NotNull Long codUnidade, @NotNull Long codAfericao) throws Throwable {
        return getIntegradorProLog().getAfericaoByCodigo(codUnidade, codAfericao);
    }

    @NotNull
    @Override
    public List<AfericaoPlaca> getAfericoesPlacas(@NotNull Long codUnidade,
                                                  @NotNull String codTipoVeiculo,
                                                  @NotNull String placaVeiculo,
                                                  @NotNull LocalDate dataInicial,
                                                  @NotNull LocalDate dataFinal,
                                                  int limit,
                                                  long offset) throws Throwable {
        return getIntegradorProLog()
                .getAfericoesPlacas(codUnidade, codTipoVeiculo, placaVeiculo, dataInicial, dataFinal, limit, offset);
    }

    @NotNull
    @Override
    public ResultInsertModeloChecklist insertModeloChecklist(
            @NotNull final ModeloChecklistInsercao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean statusAtivo,
            @NotNull final String userToken) throws Throwable {
        return getIntegradorProLog().insertModeloChecklist(modeloChecklist, checklistOfflineListener, statusAtivo, userToken);
    }

    @Override
    public void updateModeloChecklist(
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            @NotNull final ModeloChecklistEdicao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean sobrescreverPerguntasAlternativas,
            @NotNull final String userToken) throws Throwable {
        getIntegradorProLog().updateModeloChecklist(
                codUnidade,
                codModelo,
                modeloChecklist,
                checklistOfflineListener,
                sobrescreverPerguntasAlternativas,
                userToken);
    }

    @NotNull
    @Override
    public NovoChecklistHolder getNovoChecklistHolder(@NotNull Long codUnidadeModelo,
                                                      @NotNull Long codModelo,
                                                      @NotNull String placaVeiculo,
                                                      char tipoChecklist) throws Exception {
        return getIntegradorProLog().getNovoChecklistHolder(codUnidadeModelo, codModelo, placaVeiculo, tipoChecklist);
    }

    @NotNull
    @Override
    public Long insertChecklist(@NotNull final ChecklistInsercao checklist) throws Throwable {
        return getIntegradorProLog().insertChecklist(checklist);
    }

    @NotNull
    @Override
    public Checklist getChecklistByCodigo(@NotNull Long codChecklist) throws Exception {
        return getIntegradorProLog().getChecklistByCodigo(codChecklist);
    }

    @NotNull
    @Override
    public List<Checklist> getChecklistsByColaborador(@NotNull final Long cpf,
                                                      @NotNull final Long dataInicial,
                                                      @NotNull final Long dataFinal,
                                                      final int limit,
                                                      final long offset, boolean resumido) throws Exception {
        return getIntegradorProLog().getChecklistsByColaborador(cpf, dataInicial, dataFinal, limit, offset, resumido);
    }

    @NotNull
    @Override
    public List<Checklist> getTodosChecklists(@NotNull final Long codUnidade,
                                              @Nullable final Long codEquipe,
                                              @Nullable final Long codTipoVeiculo,
                                              @Nullable final String placaVeiculo,
                                              final long dataInicial,
                                              final long dataFinal,
                                              final int limit,
                                              final long offset,
                                              final boolean resumido) throws Exception {
        return getIntegradorProLog()
                .getTodosChecklists(
                        codUnidade,
                        codEquipe,
                        codTipoVeiculo,
                        placaVeiculo,
                        dataInicial,
                        dataFinal,
                        limit,
                        offset,
                        resumido);
    }

    @NotNull
    @Override
    public DeprecatedFarolChecklist getFarolChecklist(@NotNull final Long codUnidade,
                                                      @NotNull final LocalDate dataInicial,
                                                      @NotNull final LocalDate dataFinal,
                                                      final boolean itensCriticosRetroativos) throws Throwable {
        return getIntegradorProLog().getFarolChecklist(codUnidade, dataInicial, dataFinal, itensCriticosRetroativos);
    }

    @Override
    public void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable {
        getIntegradorProLog().resolverItem(item);
    }

    @Override
    public void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable {
        getIntegradorProLog().resolverItens(itensResolucao);
    }

    @NotNull
    protected IntegradorProLog getIntegradorProLog() {
        return integradorProLog;
    }

    @NotNull
    protected SistemaKey getSistemaKey() {
        return sistemaKey;
    }

    @NotNull
    protected String getUserToken() {
        return userToken;
    }
}