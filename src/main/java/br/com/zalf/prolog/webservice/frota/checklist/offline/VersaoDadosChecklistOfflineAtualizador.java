package br.com.zalf.prolog.webservice.frota.checklist.offline;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.permissao.pilares.Pilares;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 29/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class VersaoDadosChecklistOfflineAtualizador implements DadosChecklistOfflineChangedListener {

    // ================================================
    // MÉTODOS DE ATUALIZAÇÃO DE COLABORADORES
    // ================================================
    @Override
    public void onInsertColaborador(@NotNull final Connection connection,
                                    @NotNull final Long codColaborador) throws Throwable {
        if (hasPermissionTo(connection, codColaborador, Pilares.Frota.Checklist.REALIZAR)) {
            incrementaVersaoDadosUnidadeFromColaborador(connection, codColaborador);
        }
    }

    private void incrementaVersaoDadosUnidadeFromColaborador(@NotNull final Connection connection,
                                                             @NotNull final Long codColaborador) {
    }

    private boolean hasPermissionTo(@NotNull final Connection connection,
                                    @NotNull final Long codColaborador,
                                    final int codPermissao) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = connection.prepareStatement("");
            stmt.setLong(1, codColaborador);
            stmt.setInt(1, codPermissao);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("TEM_PERMISSAO");
            } else {
                throw new SQLException("Erro ao verificar a existência de permissão:\n" +
                        "codColaborador: " + codColaborador + "\n" +
                        "codPermissao: " + codPermissao);
            }
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }


    @Override
    public void onUpdateColaborador(@NotNull final Connection connection,
                                    @NotNull final Long codColaborador) throws Throwable {

    }

    @Override
    public void onUpdateStatusColaborador(@NotNull final Connection connection,
                                          @NotNull final Long codColaborador) throws Throwable {

    }

    @Override
    public void onDeleteColaborador(@NotNull final Connection connection,
                                    @NotNull final Long codColaborador) throws Throwable {

    }

    // ================================================
    // MÉTODOS DE ATUALIZAÇÃO DE VEICULOS
    // ================================================
    @Override
    public void onInsertVeiculo(@NotNull final Connection connection,
                                @NotNull final Long codVeiculo) throws Throwable {

    }

    @Override
    public void onUpdateVeiculo(@NotNull final Connection connection,
                                @NotNull final Long codVeiculo) throws Throwable {

    }

    @Override
    public void onUpdateStatusVeiculo(@NotNull final Connection connection,
                                      @NotNull final Long codVeiculo) throws Throwable {

    }

    @Override
    public void onDeleteVeiculo(@NotNull final Connection connection,
                                @NotNull final Long codVeiculo) throws Throwable {

    }

    // ================================================
    // MÉTODOS DE ATUALIZAÇÃO DE MODELOS DE CHECKLIST
    // ================================================
    @Override
    public void onInsertModeloChecklist(@NotNull final Connection connection,
                                        @NotNull final Long codModeloChecklist) throws Throwable {

    }

    @Override
    public void onUpdateModeloChecklist(@NotNull final Connection connection,
                                        @NotNull final Long codModeloChecklist) throws Throwable {

    }

    @Override
    public void onUpdateStatusModeloChecklist(@NotNull final Connection connection,
                                              @NotNull final Long codModeloChecklist) throws Throwable {

    }
}
