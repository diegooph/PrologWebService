package br.com.zalf.prolog.webservice.gente.treinamento.relatorios;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Created on 14/03/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TreinamentoRelatorioDaoImpl extends DatabaseConnection implements TreinamentoRelatorioDao {

    @Override
    public void getRelatorioEstratificadoPorColaboradorCsv(@NotNull final OutputStream outputStream,
                                                           @NotNull final Long codUnidade,
                                                           @NotNull final LocalDate dataInicial,
                                                           @NotNull final LocalDate dataFinal) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_RELATORIO_TREINAMENTO_VISUALIZADOS_POR_COLABORADOR(?, ?, ?, ?);");
            final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId();
            stmt.setObject(1, dataInicial);
            stmt.setObject(2, dataFinal);
            stmt.setString(3, zoneId);
            stmt.setLong(4, codUnidade);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }
}
