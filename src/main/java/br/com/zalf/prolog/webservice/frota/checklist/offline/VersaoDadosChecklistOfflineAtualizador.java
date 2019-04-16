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
        // Se o colaborador não tem permissão para realizar checklist, nada precisamos fazer.
        if (hasPermissionTo(connection, codColaborador, Pilares.Frota.Checklist.REALIZAR)) {
            incrementaVersaoDadosUnidadeFromColaborador(connection, codColaborador);
        }
    }

    @Override
    public void onUpdateColaborador(@NotNull final Connection connection,
                                    @NotNull final Long codColaborador) throws Throwable {
        // Se o colaborador não tem permissão para realizar checklist, nada precisamos fazer.
        if (hasPermissionTo(connection, codColaborador, Pilares.Frota.Checklist.REALIZAR)) {
            incrementaVersaoDadosUnidadeFromColaborador(connection, codColaborador);
        }
    }

    @Override
    public void onUpdateStatusColaborador(@NotNull final Connection connection,
                                          @NotNull final Long codColaborador) throws Throwable {
        // Se o colaborador não tem permissão para realizar checklist, nada precisamos fazer.
        if (hasPermissionTo(connection, codColaborador, Pilares.Frota.Checklist.REALIZAR)) {
            incrementaVersaoDadosUnidadeFromColaborador(connection, codColaborador);
        }
    }

    @Override
    public void onDeleteColaborador(@NotNull final Connection connection,
                                    @NotNull final Long codColaborador) throws Throwable {
        // Se o colaborador não tem permissão para realizar checklist, nada precisamos fazer.
        if (hasPermissionTo(connection, codColaborador, Pilares.Frota.Checklist.REALIZAR)) {
            incrementaVersaoDadosUnidadeFromColaborador(connection, codColaborador);
        }
    }

    // ================================================
    // MÉTODOS DE ATUALIZAÇÃO DE VEICULOS
    // ================================================
    @Override
    public void onInsertVeiculo(@NotNull final Connection connection,
                                @NotNull final Long codVeiculo) throws Throwable {
        // Se o veículo não está associado a nenhum modelo de checklist, nada precisamos fazer.
        if (hasVinculoWithModeloChecklist(connection, codVeiculo)) {
            incrementaVersaoDadosUnidadeFromVeiculo(connection, codVeiculo);
        }
    }

    @Override
    public void onUpdateVeiculo(@NotNull final Connection connection,
                                @NotNull final Long codVeiculo,
                                final long kmAntigoVeiculo,
                                final long kmNovoVeiculo) throws Throwable {
        // Só iremos incrementar a versão dos dados se a placa que sofreu edição está vinculada a um
        // modelo de checklist e se o KM inserido na placa for menor que o anterior.
        if (hasVinculoWithModeloChecklist(connection, codVeiculo)
                && kmNovoVeiculo < kmAntigoVeiculo) {
            incrementaVersaoDadosUnidadeFromVeiculo(connection, codVeiculo);
        }
    }

    @Override
    public void onUpdateStatusVeiculo(@NotNull final Connection connection,
                                      @NotNull final Long codVeiculo) throws Throwable {
        // Se o veículo não está associado a nenhum modelo de checklist, nada precisamos fazer.
        if (hasVinculoWithModeloChecklist(connection, codVeiculo)) {
            incrementaVersaoDadosUnidadeFromVeiculo(connection, codVeiculo);
        }
    }

    @Override
    public void onDeleteVeiculo(@NotNull final Connection connection,
                                @NotNull final Long codVeiculo) throws Throwable {
        // Se o veículo não está associado a nenhum modelo de checklist, nada precisamos fazer.
        if (hasVinculoWithModeloChecklist(connection, codVeiculo)) {
            incrementaVersaoDadosUnidadeFromVeiculo(connection, codVeiculo);
        }
    }

    // ================================================
    // MÉTODOS DE ATUALIZAÇÃO DE MODELOS DE CHECKLIST
    // ================================================
    @Override
    public void onInsertModeloChecklist(@NotNull final Connection connection,
                                        @NotNull final Long codModeloChecklist) throws Throwable {
        // Sempre que uma alteração no checklist for realizada, deveremos incrementar a versão dos dados.
        incrementaVersaoDadosUnidadeFromModeloChecklist(connection, codModeloChecklist);
    }

    @Override
    public void onUpdateModeloChecklist(@NotNull final Connection connection,
                                        @NotNull final Long codModeloChecklist) throws Throwable {
        // Sempre que uma alteração no checklist for realizada, deveremos incrementar a versão dos dados.
        incrementaVersaoDadosUnidadeFromModeloChecklist(connection, codModeloChecklist);
    }

    @Override
    public void onUpdateStatusModeloChecklist(@NotNull final Connection connection,
                                              @NotNull final Long codModeloChecklist) throws Throwable {
        // Sempre que uma alteração no checklist for realizada, deveremos incrementar a versão dos dados.
        incrementaVersaoDadosUnidadeFromModeloChecklist(connection, codModeloChecklist);
    }

    @Override
    public void onCargoAtualizado(@NotNull final Connection connection,
                                  @NotNull final Long codUnidade,
                                  @NotNull final Long codCargoAtualizado,
                                  final boolean tinhaPermissaoRealizarChecklist,
                                  final boolean temPermissaoRealizarChecklist) throws Throwable {
        // Se o cargo TINHA permissão para realizar checklist e foi removido ou se o cargo NÃO TINHA permissão
        // para realizar checklist e recebeu ela, então devemos incrementar a 'versão dos dados'.
        if ((tinhaPermissaoRealizarChecklist && !temPermissaoRealizarChecklist)
                || (temPermissaoRealizarChecklist && !tinhaPermissaoRealizarChecklist)) {
            incrementaVersaoDadosUnidade(connection, codUnidade);
        }
    }

    // ================================================
    // PRIVATE METHODS
    // ================================================
    private void incrementaVersaoDadosUnidade(@NotNull final Connection connection,
                                              @NotNull final Long codUnidade) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = connection.prepareStatement("SELECT * FROM " +
                    "FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE(?) AS VERSAO_DADOS;");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                if (rSet.getLong("VERSAO_DADOS") <= 0) {
                    throw new SQLException("A atualização da 'versão dos dados' não ocorreu como deveria.");
                }
            } else {
                throw new SQLException("Erro ao criar ou atualizar 'versao dos dados' para a unidade:\n" +
                        "codUnidade: " + codUnidade);
            }
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    private void incrementaVersaoDadosUnidadeFromModeloChecklist(
            @NotNull final Connection connection,
            @NotNull final Long codModeloChecklist) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = connection.prepareStatement("SELECT * FROM " +
                    "FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE_MODELO_CHECK(?) AS VERSAO_DADOS;");
            stmt.setLong(1, codModeloChecklist);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                if (rSet.getLong("VERSAO_DADOS") <= 0) {
                    throw new SQLException("A atualização da 'versão dos dados' não ocorreu como deveria.");
                }
            } else {
                throw new SQLException("Erro ao criar ou atualizar 'versao dos dados' para o modelo de checklist:\n" +
                        "codModeloChecklist: " + codModeloChecklist);
            }
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    private boolean hasPermissionTo(@NotNull final Connection connection,
                                    @NotNull final Long codColaborador,
                                    final int codPermissao) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = connection.prepareStatement("SELECT * FROM " +
                    "FUNC_VERIFICA_COLABORADOR_POSSUI_FUNCAO_PROLOG(?, ?) AS TEM_PERMISSAO;");
            stmt.setLong(1, codColaborador);
            stmt.setInt(2, codPermissao);
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

    private void incrementaVersaoDadosUnidadeFromColaborador(@NotNull final Connection connection,
                                                             @NotNull final Long codColaborador) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = connection.prepareStatement("SELECT * FROM " +
                    "FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE_COLABORADOR(?) AS VERSAO_DADOS;");
            stmt.setLong(1, codColaborador);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                if (rSet.getLong("VERSAO_DADOS") <= 0) {
                    throw new SQLException("A atualização da 'versão dos dados' não ocorreu como deveria.");
                }
            } else {
                throw new SQLException("Erro ao criar ou atualizar 'versao dos dados' para o colaborador:\n" +
                        "codColaborador: " + codColaborador);
            }
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    private boolean hasVinculoWithModeloChecklist(@NotNull final Connection connection,
                                                  @NotNull final Long codVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = connection.prepareStatement("SELECT * " +
                    "FROM FUNC_VERIFICA_VEICULO_POSSUI_VINCULO_MODELO_CHECKLIST(?) AS ESTA_VINCULADO_CHECKLIST;");
            stmt.setLong(1, codVeiculo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("ESTA_VINCULADO_CHECKLIST");
            } else {
                throw new SQLException("Erro ao verificar se o veículo está vinculado a algum modelo de checklist:\n" +
                        "codVeiculo: " + codVeiculo);
            }
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    private void incrementaVersaoDadosUnidadeFromVeiculo(@NotNull final Connection connection,
                                                         @NotNull final Long codVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = connection.prepareStatement("SELECT * FROM " +
                    "FUNC_CHECKLIST_OFFLINE_UPDATE_VERSAO_DADOS_UNIDADE_VEICULO(?) AS VERSAO_DADOS;");
            stmt.setLong(1, codVeiculo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                if (rSet.getLong("VERSAO_DADOS") <= 0) {
                    throw new SQLException("A atualização da 'versão dos dados' não ocorreu como deveria.");
                }
            } else {
                throw new SQLException("Erro ao criar ou atualizar 'versao dos dados' para o veiculo:\n" +
                        "codVeiculo: " + codVeiculo);
            }
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }
}
