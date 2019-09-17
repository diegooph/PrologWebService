package br.com.zalf.prolog.webservice.integracao.router;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistResource;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.Servico;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.VeiculoServico;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import br.com.zalf.prolog.webservice.integracao.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.operacoes.OperacoesIntegradas;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemasFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Os Routers são as classes responsáveis por direcionar o fluxo de um request depois do mesmo atingir o servidor do
 * ProLog. O que ele faz, na verdade, é verificar se a empresa do usuário que faz o request possui integração no recurso
 * que está sendo consumido. Por exemplo, se um usuário da Avilan consome um método do {@link ChecklistResource}, como
 * esse recurso possui integração com alguma empresa, o request pode ir parar em um Router (depende o método). Esse
 * Router irá verificar se a Avilan possui integração com o {@link RecursoIntegrado#CHECKLIST} e, se possuir,
 * instanciará a subclasse de {@link Sistema} correta para processar a requisição.
 */
public abstract class Router implements OperacoesIntegradas {
    @NotNull
    private final IntegracaoDao integracaoDao;
    @NotNull
    private final IntegradorProLog integradorProLog;
    @NotNull
    private final String userToken;
    @NotNull
    private final RecursoIntegrado recursoIntegrado;
    @Nullable
    private SistemaKey sistemaKey;
    private boolean hasTried;

    Router(@NotNull final IntegracaoDao integracaoDao,
           @NotNull final IntegradorProLog integradorProLog,
           @NotNull final String userToken,
           @NotNull final RecursoIntegrado recursoIntegrado) {
        checkNotNull(userToken, "userToken não pode ser null!");
        this.integracaoDao = checkNotNull(integracaoDao, "integracaoDao não pode ser null!");
        this.integradorProLog = checkNotNull(integradorProLog, "integradorProLog não pode ser null!");
        this.userToken = TokenCleaner.getOnlyToken(userToken);
        this.recursoIntegrado = checkNotNull(recursoIntegrado, "recursoIntegrado não pode ser null!");
    }

    @Override
    public boolean insert(
            @NotNull final Long codUnidade,
            @NotNull final Veiculo veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        if (getSistema() != null) {
            return getSistema().insert(codUnidade, veiculo, checklistOfflineListener);
        } else {
            return integradorProLog.insert(codUnidade, veiculo, checklistOfflineListener);
        }
    }

    @Override
    public boolean update(
            @NotNull final String placaOriginal,
            @NotNull final Veiculo veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        if (getSistema() != null) {
            return getSistema().update(placaOriginal, veiculo, checklistOfflineListener);
        } else {
            return integradorProLog.update(placaOriginal, veiculo, checklistOfflineListener);
        }
    }

    @Override
    public void updateStatus(
            @NotNull final Long codUnidade,
            @NotNull final String placa,
            @NotNull final Veiculo veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        if (getSistema() != null) {
            getSistema().updateStatus(codUnidade, placa, veiculo, checklistOfflineListener);
        } else {
            integradorProLog.updateStatus(codUnidade, placa, veiculo, checklistOfflineListener);
        }
    }

    @Override
    public boolean delete(
            @NotNull final String placa,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        if (getSistema() != null) {
            return getSistema().delete(placa, checklistOfflineListener);
        } else {
            return integradorProLog.delete(placa, checklistOfflineListener);
        }
    }

    @NotNull
    @Override
    public Long insertProcessoTranseferenciaVeiculo(
            @NotNull final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo,
            @NotNull final DadosChecklistOfflineChangedListener dadosChecklistOfflineChangedListener) throws Throwable {
        if (getSistema() != null) {
            return getSistema()
                    .insertProcessoTranseferenciaVeiculo(
                            processoTransferenciaVeiculo,
                            dadosChecklistOfflineChangedListener);
        } else {
            return integradorProLog
                    .insertProcessoTranseferenciaVeiculo(
                            processoTransferenciaVeiculo,
                            dadosChecklistOfflineChangedListener);
        }
    }

    @NotNull
    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull Long codUnidade, @Nullable Boolean ativos) throws
            Exception {
        if (getSistema() != null) {
            return getSistema().getVeiculosAtivosByUnidade(codUnidade, ativos);
        } else {
            return integradorProLog.getVeiculosAtivosByUnidade(codUnidade, ativos);
        }
    }

    @NotNull
    @Override
    public List<TipoVeiculo> getTiposVeiculosByEmpresa(@NotNull Long codEmpresa) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getTiposVeiculosByEmpresa(codEmpresa);
        } else {
            return integradorProLog.getTiposVeiculosByEmpresa(codEmpresa);
        }
    }

    @NotNull
    @Override
    public List<String> getPlacasVeiculosByTipo(@NotNull Long codUnidade, @NotNull String codTipo) throws Exception {
        if (getSistema() != null) {
            return getSistema().getPlacasVeiculosByTipo(codUnidade, codTipo);
        } else {
            return integradorProLog.getPlacasVeiculosByTipo(codUnidade, codTipo);
        }
    }

    @NotNull
    @Override
    public Veiculo getVeiculoByPlaca(@NotNull String placa, boolean withPneus) throws Exception {
        if (getSistema() != null) {
            return getSistema().getVeiculoByPlaca(placa, withPneus);
        } else {
            return integradorProLog.getVeiculoByPlaca(placa, withPneus);
        }
    }

    @NotNull
    @Override
    public CronogramaAfericao getCronogramaAfericao(@NotNull final Long codUnidade) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getCronogramaAfericao(codUnidade);
        } else {
            return integradorProLog.getCronogramaAfericao(codUnidade);
        }
    }

    @NotNull
    @Override
    public NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final Long codUnidade,
                                                  @NotNull final String placaVeiculo,
                                                  @NotNull final String tipoAfericao) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getNovaAfericaoPlaca(codUnidade, placaVeiculo, tipoAfericao);
        } else {
            return integradorProLog.getNovaAfericaoPlaca(codUnidade, placaVeiculo, tipoAfericao);
        }
    }

    @NotNull
    @Override
    public List<PneuAfericaoAvulsa> getPneusAfericaoAvulsa(@NotNull final Long codUnidade) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getPneusAfericaoAvulsa(codUnidade);
        } else {
            return integradorProLog.getPneusAfericaoAvulsa(codUnidade);
        }
    }

    @NotNull
    @Override
    public Report getAfericoesAvulsas(@NotNull final Long codUnidade,
                                      @Nullable final Long codColaborador,
                                      @NotNull final LocalDate dataInicial,
                                      @NotNull final LocalDate dataFinal) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getAfericoesAvulsas(codUnidade, codColaborador, dataInicial, dataFinal);
        } else {
            return integradorProLog.getAfericoesAvulsas(codUnidade, codColaborador, dataInicial, dataFinal);
        }
    }

    @Nullable
    @Override
    public Long insertAfericao(@NotNull final Long codUnidade, @NotNull final Afericao afericao) throws Throwable {
        if (getSistema() != null) {
            return getSistema().insertAfericao(codUnidade, afericao);
        } else {
            return integradorProLog.insertAfericao(codUnidade, afericao);
        }
    }

    @NotNull
    @Override
    public Afericao getAfericaoByCodigo(@NotNull Long codUnidade, @NotNull Long codAfericao) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getAfericaoByCodigo(codUnidade, codAfericao);
        } else {
            return integradorProLog.getAfericaoByCodigo(codUnidade, codAfericao);
        }
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
        if (getSistema() != null) {
            return getSistema().getAfericoesPlacas(
                    codUnidade,
                    codTipoVeiculo,
                    placaVeiculo,
                    dataInicial,
                    dataFinal,
                    limit,
                    offset);
        } else {
            return integradorProLog.getAfericoesPlacas(
                    codUnidade,
                    codTipoVeiculo,
                    placaVeiculo,
                    dataInicial,
                    dataFinal,
                    limit,
                    offset);
        }
    }

    @Override
    public void insertModeloChecklist(
            @NotNull final ModeloChecklistInsercao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean statusAtivo) throws Throwable {
        if (getSistema() != null) {
            getSistema().insertModeloChecklist(modeloChecklist, checklistOfflineListener, statusAtivo);
        } else {
            integradorProLog.insertModeloChecklist(modeloChecklist, checklistOfflineListener, statusAtivo);
        }
    }

    @Override
    public void updateModeloChecklist(
            @NotNull final String token,
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            @NotNull final ModeloChecklistEdicao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean sobrescreverPerguntasAlternativas) throws Throwable {
        if (getSistema() != null) {
            getSistema().updateModeloChecklist(
                    token,
                    codUnidade,
                    codModelo,
                    modeloChecklist,
                    checklistOfflineListener,
                    sobrescreverPerguntasAlternativas);
        } else {
            integradorProLog.updateModeloChecklist(
                    token,
                    codUnidade,
                    codModelo,
                    modeloChecklist,
                    checklistOfflineListener,
                    sobrescreverPerguntasAlternativas);
        }
    }

    @NotNull
    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@NotNull Long codUnidade,
                                                                                    @NotNull Long codFuncao) throws
            Exception {
        if (getSistema() != null) {
            return getSistema().getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
        } else {
            return integradorProLog.getSelecaoModeloChecklistPlacaVeiculo(codUnidade, codFuncao);
        }
    }

    @NotNull
    @Override
    public NovoChecklistHolder getNovoChecklistHolder(@NotNull Long codUnidadeModelo,
                                                      @NotNull Long codModelo,
                                                      @NotNull String placaVeiculo,
                                                      char tipoChecklist) throws Exception {
        if (getSistema() != null) {
            return getSistema().getNovoChecklistHolder(codUnidadeModelo, codModelo, placaVeiculo, tipoChecklist);
        } else {
            return integradorProLog.getNovoChecklistHolder(codUnidadeModelo, codModelo, placaVeiculo, tipoChecklist);
        }
    }

    @NotNull
    @Override
    public Long insertChecklist(@NotNull Checklist checklist) throws Throwable {
        if (getSistema() != null) {
            return getSistema().insertChecklist(checklist);
        } else {
            return integradorProLog.insertChecklist(checklist);
        }
    }

    @NotNull
    @Override
    public Checklist getChecklistByCodigo(@NotNull Long codChecklist) throws Exception {
        if (getSistema() != null) {
            return getSistema().getChecklistByCodigo(codChecklist);
        } else {
            return integradorProLog.getChecklistByCodigo(codChecklist);
        }
    }

    @NotNull
    @Override
    public List<Checklist> getChecklistsByColaborador(@NotNull final Long cpf,
                                                      @NotNull final Long dataInicial,
                                                      @NotNull final Long dataFinal,
                                                      final int limit,
                                                      final long offset,
                                                      final boolean resumido) throws Exception {
        if (getSistema() != null) {
            return getSistema().getChecklistsByColaborador(cpf, dataInicial, dataFinal, limit, offset, resumido);
        } else {
            return integradorProLog.getChecklistsByColaborador(cpf, dataInicial, dataFinal, limit, offset, resumido);
        }
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
        if (getSistema() != null) {
            return getSistema()
                    .getTodosChecklists(codUnidade, codEquipe, codTipoVeiculo, placaVeiculo, dataInicial, dataFinal,
                            limit, offset, resumido);
        } else {
            return integradorProLog
                    .getTodosChecklists(codUnidade, codEquipe, codTipoVeiculo, placaVeiculo, dataInicial, dataFinal,
                            limit, offset, resumido);
        }
    }

    @NotNull
    @Override
    public DeprecatedFarolChecklist getFarolChecklist(@NotNull final Long codUnidade,
                                                      @NotNull final LocalDate dataInicial,
                                                      @NotNull final LocalDate dataFinal,
                                                      final boolean itensCriticosRetroativos) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getFarolChecklist(codUnidade, dataInicial, dataFinal, itensCriticosRetroativos);
        } else {
            return integradorProLog.getFarolChecklist(codUnidade, dataInicial, dataFinal, itensCriticosRetroativos);
        }
    }

    @Override
    public void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable {
        if (getSistema() != null) {
            getSistema().resolverItem(item);
        } else {
            integradorProLog.resolverItem(item);
        }
    }

    @Override
    public void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable {
        if (getSistema() != null) {
            getSistema().resolverItens(itensResolucao);
        } else {
            integradorProLog.resolverItens(itensResolucao);
        }
    }

    @NotNull
    @Override
    public Long insert(@NotNull final Pneu pneu, @NotNull final Long codUnidade) throws Throwable {
        if (getSistema() != null) {
            return getSistema().insert(pneu, codUnidade);
        } else {
            return integradorProLog.insert(pneu, codUnidade);
        }
    }

    @NotNull
    @Override
    public List<Long> insert(@NotNull final List<Pneu> pneus) throws Throwable {
        if (getSistema() != null) {
            return getSistema().insert(pneus);
        } else {
            return integradorProLog.insert(pneus);
        }
    }

    @NotNull
    @Override
    public VeiculoServico getVeiculoAberturaServico(@NotNull final Long codServico,
                                                    @NotNull final String placaVeiculo) throws Throwable {
        if (getSistema() != null) {
            return getSistema().getVeiculoAberturaServico(codServico, placaVeiculo);
        } else {
            return integradorProLog.getVeiculoAberturaServico(codServico, placaVeiculo);
        }
    }

    @Override
    public void update(@NotNull final Pneu pneu,
                       @NotNull final Long codUnidade,
                       @NotNull final Long codOriginalPneu) throws Throwable {
        if (getSistema() != null) {
            getSistema().update(pneu, codUnidade, codOriginalPneu);
        } else {
            integradorProLog.update(pneu, codUnidade, codOriginalPneu);
        }
    }

    @NotNull
    @Override
    public Long insertTransferencia(@NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao,
                                    @NotNull final OffsetDateTime dataHoraSincronizacao,
                                    final boolean isTransferenciaFromVeiculo) throws Throwable {
        if (getSistema() != null) {
            return getSistema()
                    .insertTransferencia(
                            pneuTransferenciaRealizacao,
                            dataHoraSincronizacao,
                            isTransferenciaFromVeiculo);
        } else {
            return integradorProLog
                    .insertTransferencia(
                            pneuTransferenciaRealizacao,
                            dataHoraSincronizacao,
                            isTransferenciaFromVeiculo);
        }
    }

    @Override
    public void fechaServico(@NotNull final Long codUnidade, @NotNull final Servico servico) throws Throwable {
        if (getSistema() != null) {
            getSistema().fechaServico(codUnidade, servico);
        } else {
            integradorProLog.fechaServico(codUnidade, servico);
        }
    }

    @Nullable
    private Sistema getSistema() throws Exception {
        if (sistemaKey == null && !hasTried) {
            sistemaKey = integracaoDao.getSistemaKey(userToken, recursoIntegrado);
            hasTried = true;
        }
        if (sistemaKey == null) {
            return null;
        }

        return SistemasFactory.createSistema(sistemaKey, integradorProLog, userToken);
    }
}