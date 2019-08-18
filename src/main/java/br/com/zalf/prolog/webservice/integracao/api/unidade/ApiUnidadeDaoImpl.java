package br.com.zalf.prolog.webservice.integracao.api.unidade;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 18/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiUnidadeDaoImpl extends DatabaseConnection implements ApiUnidadeDao {
    @NotNull
    @Override
    public List<ApiUnidade> getUnidades(@NotNull final String tokenIntegracao,
                                        final boolean apenasUnidadesAtivas) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("");
            rSet = stmt.executeQuery();
            final List<ApiUnidade> unidades = new ArrayList<>();
            while (rSet.next()) {
                unidades.add(ApiUnidadeCreator.createApiUnidade(rSet));
            }
            return unidades;
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
