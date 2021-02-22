package br.com.zalf.prolog.webservice.integracao.integrador;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.MetodoIntegrado;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.IntegracaoOsFilter;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.ModelosChecklistBloqueados;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.OsIntegracao;
import br.com.zalf.prolog.webservice.integracao.integrador._model.UnidadeDeParaHolder;
import br.com.zalf.prolog.webservice.integracao.integrador._model.UnidadeRestricao;
import br.com.zalf.prolog.webservice.integracao.integrador._model.UnidadeRestricaoHolder;
import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
                                                 "WHERE TI.COD_EMPRESA = (SELECT COD_EMPRESA FROM UNIDADE WHERE " +
                                                 "CODIGO = ?);");
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

    @Override
    @NotNull
    public String getUrl(@NotNull final Long codEmpresa,
                         @NotNull final SistemaKey sistemaKey,
                         @NotNull final MetodoIntegrado metodoIntegrado) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            return getUrl(conn, codEmpresa, sistemaKey, metodoIntegrado);
        } finally {
            close(conn);
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
    public UnidadeDeParaHolder getCodAuxiliarByCodUnidadeProlog(
            @NotNull final Connection conn,
            @NotNull final List<Long> codUnidades) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement(
                    "select * from integracao.func_geral_unidade_get_infos_de_para(f_cod_unidades => ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.TEXT, codUnidades));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final UnidadeDeParaHolder unidadeDeParaHolder = IntegracaoConverter.createUnidadeDeParaHolder(rSet);
                do {
                    unidadeDeParaHolder.getUnidadesDePara().add(IntegracaoConverter.createUnidadeDePara(rSet));
                } while (rSet.next());
                return unidadeDeParaHolder;
            } else {
                throw new SQLException("Nenhum código auxiliar mapeado para as unidades:\n" +
                                               "codUnidades: " + codUnidades.toString());
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public UnidadeRestricaoHolder getUnidadeRestricaoHolder(@NotNull final Connection conn,
                                                            @NotNull final List<Long> codUnidades) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement(
                    "select * from integracao.func_pneu_afericao_get_infos_unidade_afericao(f_cod_unidades => ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Map<String, UnidadeRestricao> unidadeRestricao = new HashMap<>();
                do {
                    unidadeRestricao.put(rSet.getString("cod_auxiliar"),
                                         IntegracaoConverter.createUnidadeRestricao(rSet));
                } while (rSet.next());
                return IntegracaoConverter.createUnidadeRestricaoHolder(unidadeRestricao);
            } else {
                throw new SQLException("Nenhuma informação de restrição de unidade encontrarada para as unidades:\n" +
                                               "codUnidades: " + codUnidades.toString());
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public ApiAutenticacaoHolder getApiAutenticacaoHolder(
            @NotNull final Long codEmpresa,
            @NotNull final SistemaKey sistemaKey,
            @NotNull final MetodoIntegrado metodoIntegrado) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            return getApiAutenticacaoHolder(conn, codEmpresa, sistemaKey, metodoIntegrado);
        } finally {
            close(conn);
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
            stmt = conn.prepareStatement("select *" +
                                                 "from integracao.func_geral_busca_infos_autenticacao(" +
                                                 "f_cod_empresa => ?, " +
                                                 "f_sistema_key => ?, " +
                                                 "f_metodo_integrado => ?);");
            stmt.setLong(1, codEmpresa);
            stmt.setString(2, sistemaKey.getKey());
            stmt.setString(3, metodoIntegrado.getKey());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return new ApiAutenticacaoHolder(
                        rSet.getString("prolog_token_integracao"),
                        rSet.getString("url_completa"),
                        rSet.getString("api_token_client"),
                        rSet.getLong("api_short_code"));
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
                                                 "from integracao" +
                                                 ".func_geral_busca_unidades_bloqueadas_by_token_integracao(" +
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
                                                 "FROM INTEGRACAO.FUNC_GERAL_BUSCA_CONFIG_ABERTURA_SERVICO_PNEU" +
                                                 "(F_COD_UNIDADE => ?) " +
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

    @NotNull
    @Override
    public ModelosChecklistBloqueados getModelosChecklistBloqueados(@NotNull final Long codUnidade)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * " +
                                                 "FROM INTEGRACAO.FUNC_CHECKLIST_MODELO_GET_MODELOS_BLOQUEADOS(" +
                                                 "F_COD_UNIDADE => ?);");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<Long> codModelosBloqueados = new ArrayList<>();
                final ModelosChecklistBloqueados modelosChecklistBloqueados =
                        new ModelosChecklistBloqueados(rSet.getLong("cod_unidade"),
                                                       codModelosBloqueados);
                do {
                    modelosChecklistBloqueados
                            .getCodModelosBloqueados()
                            .add(rSet.getLong("cod_modelo_checklist"));
                } while (rSet.next());
                return modelosChecklistBloqueados;
            } else {
                return new ModelosChecklistBloqueados(codUnidade, Collections.emptyList());
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Long insertOsPendente(@NotNull final Long codUnidade, @NotNull final Long codOs) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from integracao.func_checklist_os_insert_os_pendente(" +
                                                 "f_cod_unidade => ?," +
                                                 "f_cod_os => ?) as codigo_interno_os_prolog");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codOs);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO_INTERNO_OS_PROLOG");
            } else {
                throw new SQLException("Erro ao inserir OS pendente.");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    @NotNull
    public List<OsIntegracao> getOrdensServicosIntegracaoByCod(
            @NotNull final List<Long> codsOrdensServicos,
            @NotNull final IntegracaoOsFilter integracaoOsFilter) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from integracao.func_checklist_os_busca_informacoes_os(" +
                                                 "f_cod_interno_os_prolog => ?, " +
                                                 "f_status_os => ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codsOrdensServicos));
            stmt.setString(2, integracaoOsFilter.asString());
            rSet = stmt.executeQuery();
            final List<OsIntegracao> ordensServicosIntegracao = new ArrayList<>();
            boolean isFirstLine = true;
            Long codInternoOsAntiga = null;
            long codInternoOsAtual;
            OsIntegracao osIntegracao = null;
            while (rSet.next()) {
                codInternoOsAtual = rSet.getLong("cod_interno_os_prolog");
                if (isFirstLine) {
                    osIntegracao = IntegracaoConverter.createOsIntegracao(rSet);
                    ordensServicosIntegracao.add(osIntegracao);
                    isFirstLine = false;
                    codInternoOsAntiga = codInternoOsAtual;
                }
                if (!codInternoOsAntiga.equals(codInternoOsAtual)) {
                    osIntegracao = IntegracaoConverter.createOsIntegracao(rSet);
                    ordensServicosIntegracao.add(osIntegracao);
                }
                osIntegracao.getItensNok().add(IntegracaoConverter.createItemOsIntegracao(rSet));
                codInternoOsAntiga = codInternoOsAtual;
            }
            return ordensServicosIntegracao;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<Long> buscaCodOrdensServicoPendenteSincronizacao() throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from integracao.func_checklist_os_busca_os_sincronizar();");
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<Long> codOrdensServicoParaSincronizar = new ArrayList<>();
                do {
                    codOrdensServicoParaSincronizar.add(rSet.getLong("codigo_interno_os_prolog"));
                } while (rSet.next());
                return codOrdensServicoParaSincronizar;
            } else {
                return Collections.emptyList();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void atualizaStatusOsIntegrada(@NotNull final List<Long> codsInternoOsProlog,
                                          final boolean pendente,
                                          final boolean bloqueada,
                                          final boolean incrementarTentativas) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from integracao.func_checklist_os_atualiza_status_os(" +
                                                 "f_cods_interno_os_prolog => ?, " +
                                                 "f_pendente => ?, " +
                                                 "f_bloqueada => ?, " +
                                                 "f_incrementar_tentativas => ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codsInternoOsProlog));
            stmt.setBoolean(2, pendente);
            stmt.setBoolean(3, bloqueada);
            stmt.setBoolean(4, incrementarTentativas);
            rSet = stmt.executeQuery();
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void logarStatusOsComErro(@NotNull final Long codInternoOsProlog,
                                     @NotNull final Throwable throwable) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from integracao.func_checklist_os_atualiza_erro_os(" +
                                                 "f_cod_interno_os_prolog => ?, " +
                                                 "f_error_message => ?, " +
                                                 "f_exception_logada => ?);");
            stmt.setLong(1, codInternoOsProlog);
            stmt.setString(2, throwable.getMessage());
            stmt.setString(3, ExceptionUtils.getStackTrace(throwable));
            rSet = stmt.executeQuery();
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    @NotNull
    public List<Long> buscaCodOsByCodItem(@NotNull final List<Long> codItensProlog) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from integracao.func_checklist_os_busca_codigo_os(" +
                                                 "f_cod_itens_os => ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codItensProlog));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<Long> codsInternosOSsProlog = new ArrayList<>();
                do {
                    codsInternosOSsProlog.add(rSet.getLong("cod_interno_os_prolog"));
                } while (rSet.next());
                return codsInternosOSsProlog;
            } else {
                return Collections.emptyList();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}