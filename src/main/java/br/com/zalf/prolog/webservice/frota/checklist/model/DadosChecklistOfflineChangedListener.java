package br.com.zalf.prolog.webservice.frota.checklist.model;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

/**
 * Created on 29/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface DadosChecklistOfflineChangedListener {

    void onInsertColaborador(@NotNull final Connection connection,
                             @NotNull final Long cpfColaborador) throws Throwable;

    void onUpdateColaborador(@NotNull final Connection connection,
                             @NotNull final Long cpfColaborador) throws Throwable;

    void onUpdateStatusColaborador(@NotNull final Connection connection,
                                   @NotNull final Long cpfColaborador) throws Throwable;

    void onDeleteColaborador(@NotNull final Connection connection,
                             @NotNull final Long cpfColaborador) throws Throwable;
}
