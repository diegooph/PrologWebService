package br.com.zalf.prolog.webservice.frota.checklist.offline;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

/**
 * Created on 29/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface DadosChecklistOfflineChangedListener {

    void onInsertColaborador(@NotNull final Connection connection,
                             @NotNull final Long codColaborador) throws Throwable;

    void onUpdateColaborador(@NotNull final Connection connection,
                             @NotNull final Long codColaborador) throws Throwable;

    void onUpdateStatusColaborador(@NotNull final Connection connection,
                                   @NotNull final Long codColaborador) throws Throwable;

    void onDeleteColaborador(@NotNull final Connection connection,
                             @NotNull final Long codColaborador) throws Throwable;

    void onInsertVeiculo(@NotNull final Connection connection,
                         @NotNull final Long codVeiculo) throws Throwable;

    void onUpdateVeiculo(@NotNull final Connection connection,
                         @NotNull final Long codVeiculo,
                         final long kmAntigoVeiculo,
                         final long kmNovoVeiculo) throws Throwable;

    void onVeiculosTransferidos(@NotNull final Connection connection,
                                @NotNull final Long codUnidadeOrigem,
                                @NotNull final Long codUnidadeDestino) throws Throwable;

    void onDeleteVeiculo(@NotNull final Connection connection,
                         @NotNull final Long codVeiculo) throws Throwable;

    void onInsertModeloChecklist(@NotNull final Connection connection,
                                 @NotNull final Long codModeloChecklist) throws Throwable;

    void onUpdateModeloChecklist(@NotNull final Connection connection,
                                 @NotNull final Long codModeloChecklist) throws Throwable;

    void onUpdateStatusModeloChecklist(@NotNull final Connection connection,
                                       @NotNull final Long codModeloChecklist) throws Throwable;

    void onCargoAtualizado(@NotNull final Connection connection,
                           @NotNull final Long codUnidade,
                           @NotNull final Long codCargoAtualizado,
                           final boolean tinhaPermissaoRealizarChecklist,
                           final boolean temPermissaoRealizarChecklist) throws Throwable;
}
