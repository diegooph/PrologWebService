package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo;

import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import org.jetbrains.annotations.NotNull;
import org.postgresql.util.PGobject;

import java.io.OutputStream;
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
    public void getVerificacaoPlanilhaImportVeiculoCsv(@NotNull final OutputStream out,
                                          @NotNull final Long codUnidade,
                                          @NotNull final String jsonPlanilha) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_VEICULO_CONFERE_PLANILHA_IMPORTACAO(?,?)");
            PGobject json = new PGobject();
            json.setType("json");
            json.setValue(jsonPlanilha);
            stmt.setLong(1, 5);
            stmt.setObject(2, json);
            rSet = stmt.executeQuery();
            new CsvWriter
                    .Builder(out)
                    .withResultSet(rSet)
                    .build()
                    .write();
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
