package test.routines;

import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * Essa classe move veículos e seus pneus de uma unidade para outra.
 * <p>
 * Created on 10/04/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class MigrateVeiculosUnidade extends DatabaseConnection {

    public void run(@NotNull final Map<Long, Long> codigosTiposVeiculos,
                    @NotNull final List<String> todosVeiculos,
                    @NotNull final List<Long> todosPneus,
                    @NotNull final Long codUnidadeAtualVeiculosPneus,
                    @NotNull final Long novoCodUnidadeVeiculosPneus) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            //////////////////////////////////////////////////////////////////////////////////////////
            // Migra a associação entre veículos - pneus
            stmt = conn.prepareStatement("UPDATE VEICULO_PNEU SET COD_UNIDADE = ? " +
                    "WHERE COD_PNEU::TEXT LIKE ANY (ARRAY[?]) AND COD_UNIDADE = ? ;");
            stmt.setLong(1, novoCodUnidadeVeiculosPneus);
            stmt.setArray(2, PostgresUtils.ListLongToArray(conn, todosPneus));
            stmt.setLong(3, codUnidadeAtualVeiculosPneus);
            if (stmt.executeUpdate() == 0) {
                throw new IllegalStateException("Erro ao atualizar o código da unidade na VEICULO_PNEU");
            }
            // Fim da migração da associação entre veículos - pneus
            //////////////////////////////////////////////////////////////////////////////////////////

            //////////////////////////////////////////////////////////////////////////////////////////
            // Migra os pneus para a nova unidade
            stmt = conn.prepareStatement("UPDATE PNEU P SET COD_UNIDADE = ? " +
                    "WHERE CODIGO::TEXT LIKE ANY (ARRAY[?]) AND COD_UNIDADE = ?;");
            System.out.println("Pneus que serão atualizados: " + todosPneus);
            stmt.setLong(1, novoCodUnidadeVeiculosPneus);
            stmt.setArray(2, PostgresUtils.ListLongToArray(conn, todosPneus));
            stmt.setLong(3, codUnidadeAtualVeiculosPneus);
            System.out.println("query: " + stmt.toString());
            if (stmt.executeUpdate() != todosPneus.size()) {
                throw new IllegalStateException("Erro ao atualizar a unidade e modelo do pneu");
            }
            // Fim da migração de pneus para a nova unidade
            //////////////////////////////////////////////////////////////////////////////////////////

            //////////////////////////////////////////////////////////////////////////////////////////
            // Migra os veículos para a nova unidade
            for (String placa : todosVeiculos) {
                stmt = conn.prepareStatement("SELECT V.COD_TIPO FROM VEICULO V WHERE V.PLACA = ?;");
                stmt.setString(1, placa);
                rSet = stmt.executeQuery();
                if (rSet.next()) {
                    final Long codTipoAtual = rSet.getLong("COD_TIPO");
                    stmt = conn.prepareStatement("UPDATE VEICULO SET COD_UNIDADE = ?, COD_TIPO = ? " +
                            "WHERE PLACA = ?;");
                    stmt.setLong(1, novoCodUnidadeVeiculosPneus);
                    stmt.setLong(2, codigosTiposVeiculos.get(codTipoAtual));
                    stmt.setString(3, placa);
                    if (stmt.executeUpdate() == 0) {
                        throw new IllegalStateException("Erro ao atualizar a unidade e modelo do veículo");
                    }
                } else {
                    throw new IllegalStateException("Erro ao buscar o código de tipo do veículo");
                }
            }
            // Fim da migração de veículos para a nova unidade
            //////////////////////////////////////////////////////////////////////////////////////////
            conn.commit();
        } catch (final Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            closeConnection(conn, stmt, rSet);
            throw e;
        }
    }
}