package br.com.zalf.prolog.webservice.integracao.operacoes;

import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.FarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Operações integradas do checklist.
 */
interface OperacoesIntegradasChecklist {

    Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(
            @Nonnull final Long codUnidade,
            @Nonnull final Long codFuncao) throws Exception;

    NovoChecklistHolder getNovoChecklistHolder(
            @Nonnull final Long codUnidade,
            @Nonnull final Long codModelo,
            @Nonnull final String placaVeiculo,
            final char tipoChecklist) throws Exception;

    Long insertChecklist(@Nonnull final Checklist checklist) throws Exception;

    @Nonnull
    Checklist getChecklistByCodigo(@Nonnull final Long codChecklist) throws Exception;

    @Nonnull
    List<Checklist> getChecklistsByColaborador(@Nonnull final Long cpf,
                                               @Nullable final Long dataInicial,
                                               @Nullable final Long dataFinal,
                                               final int limit,
                                               final long offset,
                                               final boolean resumido) throws Exception;

    @Nonnull
    List<Checklist> getTodosChecklists(@Nonnull final Long codUnidade,
                                       @Nullable final Long codEquipe,
                                       @Nullable final Long codTipoVeiculo,
                                       @Nullable final String placaVeiculo,
                                       final long dataInicial,
                                       final long dataFinal,
                                       final int limit,
                                       final long offset,
                                       final boolean resumido) throws Exception;

    @Nonnull
    FarolChecklist getFarolChecklist(@Nonnull final Long codUnidade,
                                     @Nonnull final Date dataInicial,
                                     @Nonnull final Date dataFinal,
                                     final boolean itensCriticosRetroativos) throws Exception;
}