package br.com.zalf.prolog.webservice.gente.treinamento.relatorios;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.CsvWriter;
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
            stmt = conn.prepareStatement("SELECT " +
                    "LPAD(tc.cpf_colaborador :: TEXT, 11, '0')                          AS CPF, " +
                    "c.nome                                                             AS COLABORADOR, " +
                    "t.descricao                                                        AS TREINAMENTO, " +
                    "to_char(tc.data_visualizacao AT TIME ZONE ?, 'DD/MM/YYYY HH24:MI') AS ULTIMA_VISUALIZACAO " +
                    "FROM treinamento_colaborador tc " +
                    "JOIN treinamento t ON tc.cod_treinamento = t.codigo " +
                    "JOIN colaborador c ON tc.cpf_colaborador = c.cpf " +
                    "WHERE T.cod_unidade = ? " +
                    "      AND tc.data_visualizacao >= ? " +
                    "      AND tc.data_visualizacao <= ? " +
                    "ORDER BY c.nome;");
            final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId();
            stmt.setString(1, zoneId);
            stmt.setLong(2, codUnidade);
            stmt.setObject(3, dataInicial);
            stmt.setObject(4, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }
}
