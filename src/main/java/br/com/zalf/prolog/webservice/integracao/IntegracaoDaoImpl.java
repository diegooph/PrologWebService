package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import com.google.common.base.Preconditions;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

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

        Log.d(TAG, String.format(
                "Empresa do colaborador %s não possui integração com %s",
                userToken,
                recursoIntegrado.getKey()));
        return null;
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
            closeConnection(conn, stmt, rSet);
        }

        throw new IllegalStateException("Código da unidade do cliente não encontrado para o código da unidade do " +
                "ProLog: " + codUnidadeProLog);
    }
}