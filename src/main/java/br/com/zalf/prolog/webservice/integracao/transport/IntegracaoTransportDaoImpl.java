package br.com.zalf.prolog.webservice.integracao.transport;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
            @NotNull final List<ItemResolvidoIntegracaoTransport> itensResolvidos) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("");
            if (stmt.executeUpdate() == itensResolvidos.size()) {
                conn.commit();
            } else {
                throw new IllegalStateException("Erro ao marcar os itens como resolvidos: "
                        + itensResolvidos.size());
            }
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
            stmt = conn.prepareStatement("SELECT * FROM FUNC_INTEGRACAO_BUSCA_ITENS_OS_EMPRESA(?, ?);");
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
