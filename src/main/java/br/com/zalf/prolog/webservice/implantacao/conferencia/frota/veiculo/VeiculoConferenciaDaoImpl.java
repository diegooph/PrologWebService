package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.close;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;

/**
 * Created on 23/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class VeiculoConferenciaDaoImpl implements VeiculoConferenciaDao {

    @Override
    public void verificarPlanilha(@NotNull final String json) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_VEICULO_CONFERE_PLANILHA_IMPLEMENTACAO(?)");

            stmt.setString(1, json);
            rSet = stmt.executeQuery();

        } finally {
            close(conn, stmt, rSet);
        }
    }
}
