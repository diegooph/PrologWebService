package br.com.zalf.prolog.webservice.integracao.autenticacao;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 13/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class AutenticacaoIntegracaoDaoImpl extends DatabaseConnection implements AutenticacaoIntegracaoDao {

    @Override
    public boolean verifyIfTokenIntegracaoExists(@NotNull final String tokenIntegracao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT EXISTS(SELECT TI.TOKEN_INTEGRACAO " +
                    "              FROM INTEGRACAO.TOKEN_INTEGRACAO TI " +
                    "              WHERE TI.TOKEN_INTEGRACAO = ?) AS EXISTE_TOKEN;");
            stmt.setString(1, tokenIntegracao);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("EXISTE_TOKEN");
            } else {
                throw new SQLException(
                        "Não foi possível verifica a existencia do token de integração: " + tokenIntegracao);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
