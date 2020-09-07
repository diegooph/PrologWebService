package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.LocalDate;

/**
 * Created on 2020-09-03
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class AvaCorpAvilanDaoImpl extends DatabaseConnection implements AvaCorpAvilanDao {
    @Override
    public void getOrdensServicosPendentesSincroniaCsv(@NotNull final OutputStream outputStream,
                                                       @Nullable final LocalDate dataInicial,
                                                       @Nullable final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * " +
                    "from integracao.func_checklist_os_busca_oss_pendentes_sincronia(" +
                    "f_data_inicio => ?," +
                    "f_data_fim => ?);");
            if (dataInicial == null) {
                stmt.setNull(1, Types.NULL);
            } else {
                stmt.setObject(1, dataInicial);
            }
            if (dataFinal == null) {
                stmt.setNull(2, Types.NULL);
            } else {
                stmt.setObject(2, dataFinal);
            }
            rSet = stmt.executeQuery();
            new CsvWriter
                    .Builder(outputStream)
                    .withResultSet(rSet)
                    .build()
                    .write();
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
