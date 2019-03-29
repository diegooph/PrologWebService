package br.com.zalf.prolog.webservice.frota.checklist.model;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

/**
 * Created on 29/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class VersaoDadosChecklistOfflineAtualizador implements DadosChecklistOfflineChangedListener {
    @Override
    public void onInsertColaborador(@NotNull final Connection connection,
                                    @NotNull final Long cpfColaborador) throws Throwable {

    }

    @Override
    public void onUpdateColaborador(@NotNull final Connection connection,
                                    @NotNull final Long cpfColaborador) throws Throwable {

    }

    @Override
    public void onUpdateStatusColaborador(@NotNull final Connection connection,
                                          @NotNull final Long cpfColaborador) throws Throwable {

    }

    @Override
    public void onDeleteColaborador(@NotNull final Connection connection,
                                    @NotNull final Long cpfColaborador) throws Throwable {

    }
}
