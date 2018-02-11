package br.com.zalf.prolog.webservice.frota.veiculo.relatorio;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtil;
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
    public int getQtdVeiculosAtivosComPneuAplicado(@NotNull final List<Long> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT count(DISTINCT v.placa) as total_veiculos \n" +
                    "FROM veiculo_pneu vp JOIN veiculo v ON v.cod_unidade = vp.cod_unidade and v.placa = vp.placa\n" +
                    "WHERE v.cod_unidade::TEXT LIKE ANY (ARRAY[?]) AND v.status_ativo IS TRUE;");
            stmt.setArray(1, PostgresUtil.ListLongToArray(conn, codUnidades));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getInt("total_veiculos");
            } else {
                return 0;
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }
}