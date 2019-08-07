package br.com.zalf.prolog.webservice.integracao.api.checklist;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 07/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiChecklistDaoImpl extends DatabaseConnection implements ApiChecklistDao {
    @NotNull
    @Override
    public List<ApiAlternativaModeloChecklist> getAlternativasModeloChecklist(
            @NotNull final String tokenIntegracao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "SELECT * FROM INTEGRACAO.FUNC_ALTERNATIVAS_MODELO_CHECKLIST(F_TOKEN_INTEGRACAO := ?);");
            stmt.setString(1, tokenIntegracao);
            rSet = stmt.executeQuery();
            final List<ApiAlternativaModeloChecklist> alternativas = new ArrayList<>();
            while (rSet.next()) {
                alternativas.add(ApiChecklistConverter.convert(rSet));
            }
            return alternativas;
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
