package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by luiz on 18/07/17.
 */
public final class IntegracaoDaoImpl extends DatabaseConnection implements IntegracaoDao {
    private static final String TAG = IntegracaoDaoImpl.class.getSimpleName();

    @Nullable
    @Override
    public SistemaKey getSistemaKey(@NotNull final String userToken,
                                    @NotNull final RecursoIntegrado recursoIntegrado) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT " +
                    "  (SELECT I.CHAVE_SISTEMA FROM INTEGRACAO I " +
                    "    JOIN TOKEN_AUTENTICACAO TA ON TA.TOKEN = ? " +
                    "    LEFT JOIN COLABORADOR C ON C.CPF = TA.CPF_COLABORADOR " +
                    "  WHERE C.COD_EMPRESA = I.COD_EMPRESA AND I.RECURSO_INTEGRADO = ?) AS CHAVE_SISTEMA, " +
                    "  (SELECT EXISTS (SELECT TOKEN FROM TOKEN_AUTENTICACAO WHERE TOKEN = ?)) AS TOKEN_EXISTE;");
            stmt.setString(1, userToken);
            stmt.setString(2, recursoIntegrado.getKey());
            stmt.setString(3, userToken);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                if (!rSet.getBoolean("TOKEN_EXISTE")) {
                    throw new Exception("Token não existe ou não é válido para a execução da funcionalidade");
                } else if (rSet.getString("CHAVE_SISTEMA") == null) {
                    return null;
                }
                return SistemaKey.fromString(rSet.getString("CHAVE_SISTEMA"));
            } else {
                Log.d(TAG, String.format(
                        "Estado de inconsistência ao buscar o recurso %s para o token %s",
                        recursoIntegrado.getKey(),
                        userToken));
                throw new IllegalStateException();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public String getCodUnidadeErpClienteByCodUnidadeProLog(@NotNull final Long codUnidadeProLog) throws SQLException {
        Preconditions.checkNotNull(codUnidadeProLog, "codUnidadeProLog não pode ser null!");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT IU.COD_UNIDADE_CLIENTE FROM INTEGRACAO_UNIDADE IU " +
                    "WHERE IU.COD_UNIDADE_PROLOG = ?");
            stmt.setLong(1, codUnidadeProLog);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getString("COD_UNIDADE_CLIENTE");
            }
        } finally {
            close(conn, stmt, rSet);
        }

        throw new IllegalStateException("Código da unidade do cliente não encontrado para o código da unidade do " +
                "ProLog: " + codUnidadeProLog);
    }

    @NotNull
    @Override
    public String getTokenIntegracaoByCodUnidadeProLog(@NotNull final Long codUnidadeProLog) throws Throwable {
        Preconditions.checkNotNull(codUnidadeProLog, "codUnidadeProLog não pode ser null!");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT TI.TOKEN_INTEGRACAO " +
                    "FROM INTEGRACAO.TOKEN_INTEGRACAO TI " +
                    "WHERE TI.COD_EMPRESA = (SELECT COD_EMPRESA FROM UNIDADE WHERE CODIGO = ?);");
            stmt.setLong(1, codUnidadeProLog);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getString("TOKEN_INTEGRACAO");
            }
        } finally {
            close(conn, stmt, rSet);
        }

        throw new IllegalStateException(
                "Nenhum token encontrado para o código da unidade do ProLog: " + codUnidadeProLog);
    }
}