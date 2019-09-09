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
            @NotNull final String tokenIntegracao,
            final boolean apenasModelosAtivos,
            final boolean apenasPerguntasAtivas,
            final boolean apenasAlternativasAtivas) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(
                    "SELECT * FROM INTEGRACAO.FUNC_CHECKLIST_ALTERNATIVAS_MODELO_CHECKLIST(" +
                            "F_TOKEN_INTEGRACAO := ?, " +
                            "F_APENAS_MODELOS_CHECKLIST_ATIVOS := ?, " +
                            "F_APENAS_PERGUNTAS_ATIVAS := ?, " +
                            "F_APENAS_ALTERNATIVAS_ATIVAS := ?);");
            stmt.setString(1, tokenIntegracao);
            stmt.setBoolean(2, apenasModelosAtivos);
            stmt.setBoolean(3, apenasPerguntasAtivas);
            stmt.setBoolean(4, apenasAlternativasAtivas);
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
