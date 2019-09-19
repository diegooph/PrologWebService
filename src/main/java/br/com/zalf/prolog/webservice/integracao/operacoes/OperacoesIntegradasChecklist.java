package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.ModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistSelecao;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
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

    NovoChecklistHolder getNovoChecklistHolder(
            @NotNull final Long codUnidade,
            @NotNull final Long codModelo,
            @NotNull final String placaVeiculo,
            final char tipoChecklist) throws Exception;

    @NotNull
    Long insertChecklist(@NotNull final ChecklistInsercao checklist) throws Throwable;

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
    DeprecatedFarolChecklist getFarolChecklist(@NotNull final Long codUnidade,
                                               @NotNull final LocalDate dataInicial,
                                               @NotNull final LocalDate dataFinal,
                                               final boolean itensCriticosRetroativos) throws Throwable;
}