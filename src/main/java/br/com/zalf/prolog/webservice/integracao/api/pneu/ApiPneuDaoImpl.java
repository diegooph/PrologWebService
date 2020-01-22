package br.com.zalf.prolog.webservice.integracao.api.pneu;

import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiStatusPneu;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiPneuAlteracaoStatus;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiPneuAlteracaoStatusVeiculo;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static br.com.zalf.prolog.webservice.commons.util.StatementUtils.bindValueOrNull;

/**
 * Created on 16/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiPneuDaoImpl extends DatabaseConnection implements ApiPneuDao {

    @Override
    public void atualizaStatusPneus(
            @NotNull final String tokenIntegracao,
            @NotNull final List<ApiPneuAlteracaoStatus> pneusAtualizacaoStatus) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            removePneusAplicados(conn, tokenIntegracao, pneusAtualizacaoStatus);
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_PNEU_ATUALIZA_STATUS_PNEU_PROLOG(" +
                    "F_COD_PNEU_SISTEMA_INTEGRADO := ?, " +
                    "F_CODIGO_PNEU_CLIENTE := ?, " +
                    "F_COD_UNIDADE_PNEU := ?, " +
                    "F_CPF_COLABORADOR_ALTERACAO_STATUS := ?, " +
                    "F_DATA_HORA_ALTERACAO_STATUS := ?, " +
                    "F_STATUS_PNEU := ?, " +
                    "F_TROCOU_DE_BANDA := ?, " +
                    "F_COD_NOVO_MODELO_BANDA_PNEU := ?, " +
                    "F_VALOR_NOVA_BANDA_PNEU := ?, " +
                    "F_PLACA_VEICULO_PNEU_APLICADO := ?, " +
                    "F_POSICAO_VEICULO_PNEU_APLICADO := ?, " +
                    "F_TOKEN_INTEGRACAO := ?) AS COD_PNEU_PROLOG;");
            for (final ApiPneuAlteracaoStatus pneuAlteracaoStatus : pneusAtualizacaoStatus) {
                stmt.setLong(1, pneuAlteracaoStatus.getCodigoSistemaIntegrado());
                stmt.setString(2, pneuAlteracaoStatus.getCodigoCliente());
                stmt.setLong(3, pneuAlteracaoStatus.getCodUnidadePneu());
                stmt.setString(4, pneuAlteracaoStatus.getCpfColaboradorAlteracaoStatus());
                stmt.setObject(5, pneuAlteracaoStatus.getDataHoraAlteracaoStatusUtc());
                stmt.setString(6, pneuAlteracaoStatus.getStatusPneu().asString());
                stmt.setBoolean(7, pneuAlteracaoStatus.isTrocouDeBanda());
                if (pneuAlteracaoStatus.isTrocouDeBanda()) {
                    bindValueOrNull(stmt, 8, pneuAlteracaoStatus.getCodNovoModeloBanda(), SqlType.BIGINT);
                    bindValueOrNull(stmt, 9, pneuAlteracaoStatus.getValorNovaBandaPneu(), SqlType.NUMERIC);
                } else {
                    stmt.setNull(8, SqlType.BIGINT.asIntTypeJava());
                    stmt.setNull(9, SqlType.NUMERIC.asIntTypeJava());
                }
                if (pneuAlteracaoStatus.getStatusPneu().equals(ApiStatusPneu.EM_USO)) {
                    final ApiPneuAlteracaoStatusVeiculo pneuAlteracaoStatusveiculo =
                            (ApiPneuAlteracaoStatusVeiculo) pneuAlteracaoStatus;
                    stmt.setString(10, pneuAlteracaoStatusveiculo.getPlacaVeiculoPneuAplicado());
                    stmt.setInt(11, pneuAlteracaoStatusveiculo.getPosicaoVeiculoPneuAplicado());
                } else {
                    stmt.setNull(10, SqlType.VARCHAR.asIntTypeJava());
                    stmt.setNull(11, SqlType.INTEGER.asIntTypeJava());
                }
                stmt.setString(12, tokenIntegracao);
                stmt.addBatch();
            }
            if (stmt.executeBatch().length != pneusAtualizacaoStatus.size()) {
                throw new SQLException("Não foi possível atualizar o status dos pneus");
            }
            conn.commit();
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            close(conn, stmt);
        }
    }

    private void removePneusAplicados(
            @NotNull final Connection conn,
            @NotNull final String tokenIntegracao,
            @NotNull final List<ApiPneuAlteracaoStatus> pneusAtualizacaoStatus) throws Throwable {
        PreparedStatement stmt = null;
        try {
            final List<Long> codSistemaIntegradoPneus =
                    ApiPneuAlteracaoStatus.getCodigoSistemaIntegradoPneus(pneusAtualizacaoStatus);
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_PNEU_REMOVE_VINCULO_PNEU_PLACA_POSICAO(" +
                    "F_TOKEN_INTEGRACAO := ?, " +
                    "F_COD_SISTEMA_INTEGRADO_PNEUS := ?)");
            stmt.setString(1, tokenIntegracao);
            stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.BIGINT, codSistemaIntegradoPneus));
            if (!stmt.execute()) {
                throw new SQLException("Não foi possível remover os vínculos dos pneus com os veículos");
            }
        } finally {
            close(stmt);
        }
    }
}
