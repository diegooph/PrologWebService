package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento;

import br.com.zalf.prolog.webservice.commons.util.StatementUtils;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.database.DatabaseUtils;
import br.com.zalf.prolog.webservice.errorhandling.Exceptions;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamento;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Created on 2020-11-03
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class VeiculoAcoplamentoDaoImpl implements VeiculoAcoplamentoDao {
    private static final int EXECUTE_BATCH_SUCCESS = 0;

    @Override
    public void removeAcoplamentoAtual(@NotNull final Connection conn,
                                       @NotNull final Long codProcessoAcoplamento) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareCall(" {call func_veiculo_remove_acoplamento_atual(" +
                    "f_cod_processo_acoplamento => ?)}");
            DatabaseUtils.bind(stmt, Lists.newArrayList(codProcessoAcoplamento));
            stmt.execute();
        } catch (final SQLException e) {
            throw Exceptions.rethrow(e);
        } finally {
            DatabaseConnection.close(stmt);
        }
    }

    @NotNull
    @Override
    public Long insertProcessoAcoplamento(@NotNull final Connection conn,
                                          @NotNull final Long codUnidadeAcoplamento,
                                          @NotNull final Long codColaboradorRealizacao,
                                          @NotNull final OffsetDateTime dataHoraAtual,
                                          @Nullable final String observacao) {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("select * from func_veiculo_insert_processo_acoplamento(" +
                    "f_cod_unidade => ?," +
                    "f_cod_colaborador_realizacao => ?," +
                    "f_data_hora_atual => ?," +
                    "f_observacao => ?);");
            DatabaseUtils.bind(stmt, Lists.newArrayList(
                    codUnidadeAcoplamento,
                    codColaboradorRealizacao,
                    dataHoraAtual,
                    StringUtils.trimToNull(observacao)));
            rSet = stmt.executeQuery();
            if (rSet.next() && rSet.getLong(1) > 0) {
                return rSet.getLong(1);
            } else {
                throw new IllegalStateException("Erro ao inserir processo de acoplamento");
            }
        } catch (final SQLException e) {
            throw Exceptions.rethrow(e);
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    @Override
    public void insertHistoricoAcoplamentos(@NotNull final Connection conn,
                                            @NotNull final Long codProcessoAcoplamento,
                                            @NotNull final List<VeiculoAcoplamento> acoplamentos) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareCall(" {call func_veiculo_insert_historico_acoplamento(" +
                    "f_cod_processo_acoplamento => ?," +
                    "f_cod_veiculo => ?," +
                    "f_cod_diagrama_veiculo => ?," +
                    "f_posicao_acao_realizada => ?," +
                    "f_veiculo_motorizado => ?," +
                    "f_km_coletado => ?," +
                    "f_acao_realizada => ?)}");
            for (final VeiculoAcoplamento acoplamento : acoplamentos) {
                DatabaseUtils.bind(stmt, Lists.newArrayList(
                        codProcessoAcoplamento,
                        acoplamento.getCodVeiculo(),
                        acoplamento.getCodDiagramaVeiculo(),
                        acoplamento.getPosicaoAcaoRealizada(),
                        acoplamento.getMotorizado(),
                        acoplamento.getKmColetado(),
                        acoplamento.getAcaoRealizada().asString()));
                stmt.addBatch();
            }
            StatementUtils.executeBatchAndValidate(
                    stmt,
                    acoplamentos.size(),
                    EXECUTE_BATCH_SUCCESS,
                    "Erro ao inserir histórico de acoplamentos.");
        } catch (final SQLException e) {
            throw Exceptions.rethrow(e);
        } finally {
            DatabaseConnection.close(stmt);
        }
    }

    @Override
    public void insertEstadoAtualAcoplamentos(@NotNull final Connection conn,
                                              @NotNull final Long codProcessoAcoplamento,
                                              @NotNull final Long codUnidadeAcoplamento,
                                              @NotNull final List<VeiculoAcoplamento> acoplamentos) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareCall(" {call func_veiculo_insert_estado_atual_acoplamentos(" +
                    "f_cod_processo_acoplamento => ?," +
                    "f_cod_unidade => ?," +
                    "f_cod_veiculo => ?," +
                    "f_cod_diagrama_veiculo => ?," +
                    "f_posicao_acoplamento => ?," +
                    "f_veiculo_motorizado => ?)}");
            for (final VeiculoAcoplamento acoplamento : acoplamentos) {
                DatabaseUtils.bind(stmt, Lists.newArrayList(
                        codProcessoAcoplamento,
                        codUnidadeAcoplamento,
                        acoplamento.getCodVeiculo(),
                        acoplamento.getCodDiagramaVeiculo(),
                        acoplamento.getPosicaoAcaoRealizada(),
                        acoplamento.getMotorizado()));
                stmt.addBatch();
            }
            StatementUtils.executeBatchAndValidate(
                    stmt,
                    acoplamentos.size(),
                    EXECUTE_BATCH_SUCCESS,
                    "Erro ao inserir estado atual dos acoplamentos.");
        } catch (final SQLException e) {
            throw Exceptions.rethrow(e);
        } finally {
            DatabaseConnection.close(stmt);
        }
    }
}
