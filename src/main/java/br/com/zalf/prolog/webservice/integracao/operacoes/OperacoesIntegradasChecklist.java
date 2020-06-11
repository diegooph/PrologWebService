package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.ChecklistListagem;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistRealizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistSelecao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.List;

/**
 * Operações integradas do checklist.
 */
interface OperacoesIntegradasChecklist {

    @NotNull
    ResultInsertModeloChecklist insertModeloChecklist(
            @NotNull final ModeloChecklistInsercao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean statusAtivo,
            @NotNull final String userToken) throws Throwable;

    void updateModeloChecklist(
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            @NotNull final ModeloChecklistEdicao modeloChecklist,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener,
            final boolean sobrescreverPerguntasAlternativas,
            @NotNull final String userToken) throws Throwable;

    @NotNull
    List<ModeloChecklistSelecao> getModelosSelecaoRealizacao(@NotNull final Long codUnidade,
                                                             @NotNull final Long codCargo) throws Throwable;

    @NotNull
    ModeloChecklistRealizacao getModeloChecklistRealizacao(final @NotNull Long codModeloChecklist,
                                                           final @NotNull Long codVeiculo,
                                                           final @NotNull String placaVeiculo,
                                                           final @NotNull TipoChecklist tipoChecklist) throws Throwable;

    @NotNull
    Long insertChecklist(@NotNull final ChecklistInsercao checklist,
                         final boolean foiOffline,
                         final boolean deveAbrirOs) throws Throwable;

    @NotNull
    List<TipoVeiculo> getTiposVeiculosFiltroChecklist(@NotNull final Long codEmpresa) throws Throwable;

    @NotNull
    Checklist getChecklistByCodigo(@NotNull final Long codChecklist) throws Exception;

    @NotNull
    List<Checklist> getChecklistsByColaborador(@NotNull final Long cpf,
                                               @NotNull final Long dataInicial,
                                               @NotNull final Long dataFinal,
                                               final int limit,
                                               final long offset,
                                               final boolean resumido) throws Exception;

    @NotNull
    List<Checklist> getTodosChecklists(@NotNull final Long codUnidade,
                                       @Nullable final Long codEquipe,
                                       @Nullable final Long codTipoVeiculo,
                                       @Nullable final String placaVeiculo,
                                       final long dataInicial,
                                       final long dataFinal,
                                       final int limit,
                                       final long offset,
                                       final boolean resumido) throws Exception;

    @NotNull
    List<ChecklistListagem> getListagemByColaborador(@NotNull final Long cpf,
                                                     @NotNull final LocalDate dataInicial,
                                                     @NotNull final LocalDate dataFinal,
                                                     final int limit,
                                                     final long offset) throws Throwable;

    @NotNull
    List<ChecklistListagem> getListagem(@NotNull final Long codUnidade,
                                        @Nullable final Long codEquipe,
                                        @Nullable final Long codTipoVeiculo,
                                        @Nullable final String placaVeiculo,
                                        @NotNull LocalDate dataInicial,
                                        @NotNull LocalDate dataFinal,
                                        final int limit,
                                        final long offset) throws Throwable;

    @NotNull
    DeprecatedFarolChecklist getFarolChecklist(@NotNull final Long codUnidade,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal,
                                               final boolean itensCriticosRetroativos) throws Throwable;
}