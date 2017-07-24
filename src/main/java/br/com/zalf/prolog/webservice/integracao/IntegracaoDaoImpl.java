package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.commons.util.L;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import com.sun.istack.internal.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by luiz on 18/07/17.
 */
public final class IntegracaoDaoImpl extends DatabaseConnection implements IntegracaoDao {
    private static final String TAG = IntegracaoDaoImpl.class.getSimpleName();

    @Override
    public SistemaKey getSistemaKey(@NotNull String userToken,
                                    @NotNull RecursoIntegrado recursoIntegrado) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT I.CHAVE_SISTEMA " +
                    "FROM INTEGRACAO I " +
                    "JOIN TOKEN_AUTENTICACAO TA ON TA.TOKEN = ? " +
                    "JOIN COLABORADOR C ON C.CPF = TA.CPF_COLABORADOR " +
                    "WHERE C.COD_EMPRESA = I.COD_EMPRESA AND I.RECURSO_INTEGRADO = ?");
            stmt.setString(1, userToken);
            stmt.setString(2, recursoIntegrado.getKey());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return SistemaKey.fromString(rSet.getString("CHAVE_SISTEMA"));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }

        L.d(TAG, String.format(
                "Empresa do colaborador %s não possui integração com %s",
                userToken,
                recursoIntegrado.getKey()));
        return null;
    }
}