package br.com.zalf.prolog.webservice.integracao.transport;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 03/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class IntegracaoTransportDaoImpl extends DatabaseConnection implements IntegracaoTransportDao {

    @Override
    public void resolverMultiplosItens(
            @NotNull final String tokenIntegracao,
            @NotNull final LocalDateTime dataHoraAtualUtc,
            @NotNull final List<ItemResolvidoIntegracaoTransport> itensResolvidos) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM INTEGRACAO.FUNC_INTEGRACAO_RESOLVE_ITENS_PENDENTES_EMPRESA(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            for (final ItemResolvidoIntegracaoTransport itensResolvido : itensResolvidos) {
                stmt.setLong(1, itensResolvido.getCodUnidadeOrdemServico());
                stmt.setLong(2, itensResolvido.getCodOrdemServico());
                stmt.setLong(3, itensResolvido.getCodItemResolvido());
                stmt.setLong(4, Long.parseLong(itensResolvido.getCpfColaboradoResolucao()));
                stmt.setLong(5, itensResolvido.getKmColetadoVeiculo());
                stmt.setLong(6, itensResolvido.getDuracaoResolucaoItemEmMilissegundos());
                stmt.setString(7, itensResolvido.getFeedbackResolucao());
                stmt.setObject(8, itensResolvido.getDataHoraResolvidoProLog().atOffset(ZoneOffset.UTC));
                stmt.setObject(9, itensResolvido.getDataHoraInicioResolucao().atOffset(ZoneOffset.UTC));
                stmt.setObject(10,itensResolvido.getDataHoraFimResolucao().atOffset(ZoneOffset.UTC));
                stmt.setString(11, tokenIntegracao);
                stmt.setObject(12, dataHoraAtualUtc.atOffset(ZoneOffset.UTC));
                stmt.addBatch();
            }
            // Verificamos apenas se a quantidade de vezes que a function executou bate com a quantidade de itens.
            // A function irá lançar uma exceção para qualquer caso de inconsistência, não é preciso verificar aqui
            // no java se cada vez que a function executou os updates.
            if (stmt.executeBatch().length != itensResolvidos.size()) {
                throw new SQLException("Não foi possível resolver os itens");
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

    @NotNull
    @Override
    public List<ItemPendenteIntegracaoTransport> getItensPendentes(
            @NotNull final String tokenIntegracao,
            @NotNull final Long codUltimoItemPendenteSincronizado) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_INTEGRACAO_BUSCA_ITENS_OS_EMPRESA(?, ?);");
            stmt.setLong(1, codUltimoItemPendenteSincronizado);
            stmt.setString(2, tokenIntegracao);
            rSet = stmt.executeQuery();
            final List<ItemPendenteIntegracaoTransport> itensPendentes = new ArrayList<>();
            while (rSet.next()) {
                itensPendentes.add(IntegracaoTransportConverter.convert(rSet));
            }
            return itensPendentes;
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
