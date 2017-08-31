package br.com.zalf.prolog.webservice.gente.controleintervalo.controleintervalorelatorios;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.commons.CsvWriter;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Zart on 28/08/2017.
 */
public class ControleIntervaloRelatorioDaoImpl extends DatabaseConnection implements ControleIntervaloRelatoriosDao {


    @Override
    public void getIntervalosCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
            throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getIntervalosStmt(codUnidade, dataInicial, dataFinal, cpf, conn);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private PreparedStatement getIntervalosStmt(Long codUnidade, Date dataInicial, Date dataFinal, String cpf, Connection conn)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM FUNC_RELATORIO_MARCACAO_PONTO_REALIZADOS" +
                "(?, ?, ?, ?);");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, DateUtils.toSqlDate(dataInicial));
        stmt.setDate(3, DateUtils.toSqlDate(dataFinal));
        stmt.setString(4, cpf);
        return stmt;
    }


}
