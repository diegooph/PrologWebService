package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.transport.MetodoIntegrado;
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
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_GERAL_BUSCA_SISTEMA_KEY(?, ?);");
            stmt.setString(1, userToken);
            stmt.setString(2, recursoIntegrado.getKey());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                if (!rSet.getBoolean("EXISTE_TOKEN")) {
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

    @NotNull
    @Override
    public Long getCodEmpresaByTokenIntegracao(@NotNull final Connection conn,
                                               @NotNull String tokenIntegracao) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT TI.COD_EMPRESA " +
                    "FROM INTEGRACAO.TOKEN_INTEGRACAO TI " +
                    "WHERE TI.TOKEN_INTEGRACAO = ?;");
            stmt.setString(1, tokenIntegracao);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("COD_EMPRESA");
            }
        } finally {
            close(stmt, rSet);
        }

        throw new IllegalStateException(
                "Erro ao buscar cod_empresa a partir de token da integração: " + tokenIntegracao);
    }

    @NotNull
    @Override
    public Long getCodEmpresaByCodUnidadeProLog(@NotNull final Connection conn,
                                                @NotNull final Long codUnidadeProLog) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement(
                    "SELECT U.COD_EMPRESA AS COD_EMPRESA " +
                            "FROM UNIDADE U " +
                            "WHERE U.CODIGO = ?;");
            stmt.setLong(1, codUnidadeProLog);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("COD_EMPRESA");
            } else {
                throw new SQLException("Nenhum dado retornado para a unidade:\ncodUnidadeProLog: " + codUnidadeProLog);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public String getUrl(@NotNull final Connection conn,
                         @NotNull final Long codEmpresa,
                         @NotNull final SistemaKey sistemaKey,
                         @NotNull final MetodoIntegrado metodoIntegrado) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement(
                    "SELECT * FROM INTEGRACAO.FUNC_GERAL_BUSCA_URL_SISTEMA_PARCEIRO(?, ?, ?) AS URL_COMPLETA;");
            stmt.setLong(1, codEmpresa);
            stmt.setString(2, sistemaKey.getKey());
            stmt.setString(3, metodoIntegrado.getKey());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getString("URL_COMPLETA");
            } else {
                throw new SQLException("Nenhuma URL encontrada para:\n" +
                        "codEmpresa: " + codEmpresa + "\n" +
                        "sistemaKey: " + sistemaKey.getKey() + "\n" +
                        "metodoIntegrado: " + metodoIntegrado.getKey());
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public ApiAutenticacaoHolder getApiAutenticacaoHolder(
            @NotNull final Connection conn,
            @NotNull final Long codEmpresa,
            @NotNull final SistemaKey sistemaKey,
            @NotNull final MetodoIntegrado metodoIntegrado) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT *" +
                    "FROM INTEGRACAO.FUNC_GERAL_BUSCA_INFOS_AUTENTICACAO(" +
                    "F_COD_EMPRESA := ?, " +
                    "F_SISTEMA_KEY := ?, " +
                    "F_METODO_INTEGRADO := ?);");
            stmt.setLong(1, codEmpresa);
            stmt.setString(2, sistemaKey.getKey());
            stmt.setString(3, metodoIntegrado.getKey());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return new ApiAutenticacaoHolder(
                        rSet.getString("URL_COMPLETA"),
                        rSet.getString("API_TOKEN_CLIENT"),
                        rSet.getLong("API_SHORT_CODE"));
            } else {
                throw new SQLException("Nenhuma URL encontrada para:\n" +
                        "codEmpresa: " + codEmpresa + "\n" +
                        "sistemaKey: " + sistemaKey.getKey() + "\n" +
                        "metodoIntegrado: " + metodoIntegrado.getKey());
            }
        } finally {
            close(stmt, rSet);
        }
    }
}