package br.com.zalf.prolog.webservice.frota.checklist.offline;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 10/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ChecklistOfflineDaoImpl extends DatabaseConnection implements ChecklistOfflineDao {

    @Override
    public boolean getChecklistOfflineAtivoEmpresa(@NotNull final Long cpfColaborador) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "SELECT * FROM FUNC_CHECKLIST_OFFLINE_EMPRESA_LIEBRADA(?) AS CHECKLIST_OFFLINE_LIBERADO");
            stmt.setLong(1, cpfColaborador);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("CHECKLIST_OFFLINE_LIBERADO");
            } else {
                throw new SQLException(
                        "Erro ao buscar informações se a empresa está liberada para executar checklist offline");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
