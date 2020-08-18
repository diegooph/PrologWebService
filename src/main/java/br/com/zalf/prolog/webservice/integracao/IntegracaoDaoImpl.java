package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                // Se a chave do sistema é null, significa que o usuário não tem integração, não precisamos validar mais
                // nada, apenas retornamos.
                if (rSet.getString("CHAVE_SISTEMA") == null) {
                    return null;
                }
                if (!rSet.getBoolean("EXISTE_TOKEN")) {
                    throw new Exception("Token não existe ou não é válido para a execução da funcionalidade");
                }
                if (!rSet.getBoolean("TOKEN_ATIVO")) {
                    throw new Exception("O Token está desativado");
                }
                if (!rSet.getBoolean("RECURSO_INTEGRADO_ATIVO")) {
                    throw new Exception("O recurso integrado " + recursoIntegrado + " está desativado");
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
                                               @NotNull final String tokenIntegracao) throws Throwable {
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

    @Override
    public @NotNull String getUrl(@NotNull final Long codEmpresa,
                                  @NotNull final SistemaKey sistemaKey,
                                  @NotNull final MetodoIntegrado metodoIntegrado) throws Throwable {
        final Connection conn = getConnection();
        final String url = getUrl(conn, codEmpresa, sistemaKey, metodoIntegrado);
        conn.close();
        return url;
    }

    @NotNull
    @Override
    public String getCodAuxiliarByCodUnidadeProlog(@NotNull final Connection conn,
                                                   @NotNull final Long codUnidadeProlog) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT U.COD_AUXILIAR FROM PUBLIC.UNIDADE U WHERE U.CODIGO = ?;");
            stmt.setLong(1, codUnidadeProlog);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getString("COD_AUXILIAR");
            }
            throw new SQLException("Não foi possível buscar o código auxiliar para a unidade:\n" +
                    "codUnidadeProlog: " + codUnidadeProlog);
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

    @NotNull
    @Override
    public List<Long> getCodUnidadesIntegracaoBloqueada(
            @NotNull final String userToken,
            @NotNull final SistemaKey sistemaKey,
            @NotNull final RecursoIntegrado recursoIntegrado) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * " +
                    "from integracao.func_geral_busca_unidades_bloqueadas_integracao(" +
                    "f_user_token => ?, " +
                    "f_sistema_key => ?, " +
                    "f_recurso_integrado => ?);");
            stmt.setString(1, userToken);
            stmt.setString(2, sistemaKey.getKey());
            stmt.setString(3, recursoIntegrado.getKey());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<Long> codUnidadesBloqueadas = new ArrayList<>();
                do {
                    codUnidadesBloqueadas.add(rSet.getLong("COD_UNIDADE_BLOQUEADA"));
                } while (rSet.next());
                return codUnidadesBloqueadas;
            } else {
                return Collections.emptyList();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<Long> getCodUnidadesIntegracaoBloqueadaByTokenIntegracao(
            @NotNull final String tokenIntegracao,
            @NotNull final SistemaKey sistemaKey,
            @NotNull final RecursoIntegrado recursoIntegrado) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * " +
                    "from integracao.func_geral_busca_unidades_bloqueadas_by_token_integracao(" +
                    "f_token_integracao => ?, " +
                    "f_sistema_key => ?, " +
                    "f_recurso_integrado => ?);");
            stmt.setString(1, tokenIntegracao);
            stmt.setString(2, sistemaKey.getKey());
            stmt.setString(3, recursoIntegrado.getKey());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<Long> codUnidadesBloqueadas = new ArrayList<>();
                do {
                    codUnidadesBloqueadas.add(rSet.getLong("COD_UNIDADE_BLOQUEADA"));
                } while (rSet.next());
                return codUnidadesBloqueadas;
            } else {
                return Collections.emptyList();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public boolean getConfigAberturaServicoPneuIntegracao(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM INTEGRACAO.FUNC_GERAL_BUSCA_CONFIG_ABERTURA_SERVICO_PNEU(F_COD_UNIDADE => ?) " +
                    "AS DEVE_ABRIR_SERVICO_PNEU");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("DEVE_ABRIR_SERVICO_PNEU");
            } else {
                throw new SQLException("Erro ao buscar configuração para a unidade");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void insertOsPendente(@NotNull final Long codUnidade, @NotNull final Long codOs) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * " +
                    "FROM INTEGRACAO.FUNC_CHECKLIST_INSERT_OS_PENDENTE(" +
                    "F_COD_UNIDADE => ?," +
                    "F_COD_OS => ?)");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codOs);
            rSet = stmt.executeQuery();
            if (!rSet.next()) {
                throw new SQLException("Erro ao inserir OS pendente.");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

}