package br.com.zalf.prolog.webservice.frota.veiculo.relatorio;

import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created on 1/25/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class RelatorioVeiculoDaoImpl extends DatabaseConnection implements RelatorioVeiculoDao {

    @Override
    public int getQtdVeiculosAtivos(@NotNull final List<Long> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT COUNT(DISTINCT V.PLACA) AS TOTAL_VEICULOS " +
                    "FROM VEICULO AS V " +
                    "WHERE V.COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND V.STATUS_ATIVO IS TRUE;");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.TEXT, codUnidades));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getInt("TOTAL_VEICULOS");
            } else {
                return 0;
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }
}