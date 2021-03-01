package br.com.zalf.prolog.webservice.integracao.integrador;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.AfericaoPlaca;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.ConfiguracaoNovaAfericaoAvulsa;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.ConfiguracaoNovaAfericaoPlaca;
import br.com.zalf.prolog.webservice.integracao.MetodoIntegrado;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.IntegracaoOsFilter;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.ModelosChecklistBloqueados;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.OsIntegracao;
import br.com.zalf.prolog.webservice.integracao.integrador._model.*;
import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.time.ZoneOffset;
import java.util.*;

import static br.com.zalf.prolog.webservice.commons.util.database.StatementUtils.executeBatchAndValidate;

/**
 * Created by luiz on 18/07/17.
 */
public final class IntegracaoDaoImpl extends DatabaseConnection implements IntegracaoDao {
    @NotNull
    private static final String TAG = IntegracaoDaoImpl.class.getSimpleName();
    private static final int EXECUTE_BATCH_SUCCESS = 0;

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
    public TipoVeiculoConfigAfericaoHolder getTipoVeiculoConfigAfericaoHolder(
            @NotNull final Connection conn,
            @NotNull final List<Long> codUnidades) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement(
                    "select * from " +
                            "integracao.func_pneu_afericao_get_infos_configuracao_afericao(f_cod_unidades => ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Table<String, String, TipoVeiculoConfigAfericao> tipoVeiculoConfiguracao =
                        HashBasedTable.create();
                do {
                    tipoVeiculoConfiguracao.put(
                            rSet.getString("cod_auxiliar_unidade"),
                            rSet.getString("cod_auxiliar_tipo_veiculo"),
                            IntegracaoConverter.createTipoVeiculoConfigAfericao(rSet));
                } while (rSet.next());
                return IntegracaoConverter.createTipoVeiculoConfigAfericaoHolder(tipoVeiculoConfiguracao);
            } else {
                return IntegracaoConverter.createTipoVeiculoConfigAfericaoHolder(HashBasedTable.create());
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public AfericaoRealizadaPlacaHolder getAfericaoRealizadaPlacaHolder(
            @NotNull final Connection conn,
            @NotNull final Long codEmpresa,
            @NotNull final List<String> placas) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("select * " +
                                                 "from integracao.func_pneu_afericao_get_infos_placas_aferidas(" +
                                                 "f_cod_empresa => ?, " +
                                                 "f_placas_afericao => ?, " +
                                                 "f_data_hora_atual => ?);");
            stmt.setLong(1, codEmpresa);
            stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.TEXT, placas));
            stmt.setObject(3, Now.getOffsetDateTimeUtc());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Map<String, AfericaoRealizadaPlaca> afericaoRealizadaPlaca = new HashMap<>();
                do {
                    afericaoRealizadaPlaca.put(rSet.getString("placa_afericao"),
                                               IntegracaoConverter.createAfericaoRealizadaPlaca(rSet));
                } while (rSet.next());
                return IntegracaoConverter.createAfericaoRealizadaPlacaHolder(afericaoRealizadaPlaca);
            } else {
                throw new SQLException("Nenhuma informação de aferição encontrada para as placas:\n" +
                                               "placas: " + placas.toString());
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public AfericaoRealizadaAvulsaHolder getAfericaoRealizadaAvulsaHolder(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final List<String> codPneus) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement(
                    "select * from integracao.func_pneu_afericao_get_infos_afericoes_integrada(" +
                            "f_cod_unidade => ?," +
                            "f_cod_pneus_cliente => ?);");
            stmt.setLong(1, codUnidade);
            stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.TEXT, codPneus));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<AfericaoRealizadaAvulsa> afericaoRealizadaAvulsa = new ArrayList<>();
                do {
                    afericaoRealizadaAvulsa.add(IntegracaoConverter.createAfericaoRealizadaAvulsa(rSet));
                } while (rSet.next());
                return IntegracaoConverter.createAfericaoRealizadaAvulsaHolder(afericaoRealizadaAvulsa);
            } else {
                throw new SQLException("Nenhuma informação de aferição encontrada para os pneus:\n" +
                                               "codPneus: " + codPneus.toString());
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public ConfiguracaoNovaAfericaoPlaca getConfigNovaAfericaoPlaca(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final String codEstruturaVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("select * " +
                                                 "from integracao.func_pneu_afericao_get_config_nova_afericao_placa(" +
                                                 "f_cod_unidade => ?, " +
                                                 "f_cod_auxiliar_tipo_veiculo => ?);");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, codEstruturaVeiculo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return IntegracaoConverter.createConfiguracaoNovaAfericaoPlaca(rSet);
            } else {
                throw new SQLException("Nenhuma configuração de aferição encontrada para a estrutura:\n" +
                                               "codUnidade: " + codUnidade + "\n" +
                                               "codEstruturaVeiculo: " + codEstruturaVeiculo);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public ConfiguracaoNovaAfericaoAvulsa getConfigNovaAfericaoAvulsa(@NotNull final Connection conn,
                                                                      @NotNull final Long codUnidade) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement(
                    "select * " +
                            "from integracao.func_pneu_afericao_get_config_nova_afericao_avulsa(f_cod_unidade => ?);");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return IntegracaoConverter.createConfiguracaoNovaAfericaoAvulsa(rSet);
            } else {
                throw new SQLException("Nenhuma configuração de aferição encontrada para a estrutura:\n" +
                                               "codUnidade: " + codUnidade);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Short getCodDiagramaByDeParaTipoVeiculo(@NotNull final Connection conn,
                                                   @NotNull final Long codEmpresa,
                                                   @NotNull final String codEstruturaVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement(
                    "select * from integracao.func_pneu_afericao_get_cod_diagrama_by_cod_auxiliar(" +
                            "f_cod_empresa => ?, " +
                            "f_cod_auxiliar_tipo_veiculo => ?) as cod_diagrama;");
            stmt.setLong(1, codEmpresa);
            stmt.setString(2, codEstruturaVeiculo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getShort("cod_diagrama");
            } else {
                throw new SQLException("Nenhum diagrama encontrado para a estrutura do veículo:\n" +
                                               "codEmpresa: " + codEmpresa + "\n" +
                                               "codEstruturaVeiculo: " + codEstruturaVeiculo);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Map<String, Integer> getMapeamentoPosicoesPrologByDeParaTipoVeiculo(
            @NotNull final Connection conn,
            @NotNull final Long codEmpresa,
            @NotNull final String codEstruturaVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement(
                    "select * from integracao.func_pneu_afericao_get_mapeamento_posicoes_prolog(" +
                            "f_cod_empresa => ?, " +
                            "f_cod_auxiliar_tipo_veiculo => ?);");
            stmt.setLong(1, codEmpresa);
            stmt.setString(2, codEstruturaVeiculo);
            rSet = stmt.executeQuery();
            final Map<String, Integer> posicoesPneusProlog = new HashMap<>();
            while (rSet.next()) {
                posicoesPneusProlog.put(
                        // Utilizamos o 'COD_AUXILIAR_NOMENCLATURA_CLIENTE' pois as posições dos pneus estão
                        // mapeadas no cod_auxiliar para a empresa Nepomuceno.
                        rSet.getString("cod_auxiliar_nomenclatura_cliente"),
                        rSet.getInt("posicao_prolog"));
            }
            return posicoesPneusProlog;
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

    @Override
    @NotNull
    public Long insertAfericao(@NotNull final Connection conn,
                               @NotNull final Long codUnidade,
                               @NotNull final String codAuxiliarUnidade,
                               @NotNull final Afericao afericao) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_PNEU_AFERICAO_INSERT_AFERICAO_INTEGRADA(" +
                                                 "F_COD_UNIDADE_PROLOG => ?," +
                                                 "F_COD_AUXILIAR_UNIDADE => ?," +
                                                 "F_CPF_AFERIDOR => ?, " +
                                                 "F_PLACA_VEICULO => ?::TEXT, " +
                                                 "F_COD_AUXILIAR_TIPO_VEICULO_PROLOG => ?, " +
                                                 "F_KM_VEICULO => ?::TEXT, " +
                                                 "F_TEMPO_REALIZACAO => ?, " +
                                                 "F_DATA_HORA => ?, " +
                                                 "F_TIPO_MEDICAO_COLETADA => ?, " +
                                                 "F_TIPO_PROCESSO_COLETA => ?) AS COD_AFERICAO_INTEGRADA;");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, codAuxiliarUnidade);
            stmt.setString(3, String.valueOf(afericao.getColaborador().getCpf()));
            if (afericao instanceof AfericaoPlaca) {
                final AfericaoPlaca afericaoPlaca = (AfericaoPlaca) afericao;
                stmt.setString(4, afericaoPlaca.getVeiculo().getPlaca());
                // Setamos o código auxiliar do tipo no nome do diagrama.
                stmt.setString(5, afericaoPlaca.getVeiculo().getDiagrama().getNome());
                stmt.setString(6, String.valueOf(afericaoPlaca.getKmMomentoAfericao()));
            } else {
                stmt.setNull(4, Types.VARCHAR);
                stmt.setNull(5, Types.VARCHAR);
                stmt.setNull(6, Types.VARCHAR);
            }
            stmt.setLong(7, afericao.getTempoRealizacaoAfericaoInMillis());
            stmt.setObject(8, afericao.getDataHora().atOffset(ZoneOffset.UTC));
            stmt.setString(9, afericao.getTipoMedicaoColetadaAfericao().asString());
            stmt.setString(10, afericao.getTipoProcessoColetaAfericao().asString());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codAfericaoIntegrada = rSet.getLong("COD_AFERICAO_INTEGRADA");
                internalInsertValoresAfericao(conn, codAfericaoIntegrada, afericao);
                return codAfericaoIntegrada;
            } else {
                throw new IllegalStateException(
                        "Não foi possível inserir a aferição realizada no schema de integração");
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

    @SuppressWarnings("ConstantConditions")
    private void internalInsertValoresAfericao(@NotNull final Connection conn,
                                               @NotNull final Long codAfericaoInserida,
                                               @NotNull final Afericao afericao) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(
                    "SELECT * FROM INTEGRACAO.FUNC_PNEU_AFERICAO_INSERT_AFERICAO_VALORES_INTEGRADA(" +
                            "F_COD_AFERICAO_INTEGRADA => ?::BIGINT," +
                            "F_COD_PNEU => ?::TEXT, " +
                            "F_COD_PNEU_CLIENTE => ?::TEXT, " +
                            "F_VIDA_ATUAL => ?::INTEGER, " +
                            "F_PSI => ?::REAL, " +
                            "F_ALTURA_SULCO_INTERNO => ?::REAL, " +
                            "F_ALTURA_SULCO_CENTRAL_INTERNO => ?::REAL, " +
                            "F_ALTURA_SULCO_CENTRAL_EXTERNO => ?::REAL, " +
                            "F_ALTURA_SULCO_EXTERNO => ?::REAL, " +
                            "F_POSICAO_PROLOG => ?::INTEGER) AS COD_AFERICAO_INTEGRADA;");
            for (final Pneu pneu : afericao.getPneusAferidos()) {
                stmt.setLong(1, codAfericaoInserida);
                stmt.setString(2, String.valueOf(pneu.getCodigo()));
                stmt.setString(3, String.valueOf(pneu.getCodigoCliente()));
                stmt.setInt(4, pneu.getVidaAtual());
                switch (afericao.getTipoMedicaoColetadaAfericao()) {
                    case SULCO_PRESSAO:
                        stmt.setDouble(5, pneu.getPressaoAtual());
                        stmt.setDouble(6, pneu.getSulcosAtuais().getInterno());
                        stmt.setDouble(7, pneu.getSulcosAtuais().getCentralInterno());
                        stmt.setDouble(8, pneu.getSulcosAtuais().getCentralExterno());
                        stmt.setDouble(9, pneu.getSulcosAtuais().getExterno());
                        break;
                    case SULCO:
                        stmt.setNull(5, Types.REAL);
                        stmt.setDouble(6, pneu.getSulcosAtuais().getInterno());
                        stmt.setDouble(7, pneu.getSulcosAtuais().getCentralInterno());
                        stmt.setDouble(8, pneu.getSulcosAtuais().getCentralExterno());
                        stmt.setDouble(9, pneu.getSulcosAtuais().getExterno());
                        break;
                    case PRESSAO:
                        stmt.setDouble(5, pneu.getPressaoAtual());
                        stmt.setNull(6, Types.REAL);
                        stmt.setNull(7, Types.REAL);
                        stmt.setNull(8, Types.REAL);
                        stmt.setNull(9, Types.REAL);
                        break;
                    default:
                        throw new IllegalStateException(
                                "Unexpected value: " + afericao.getTipoMedicaoColetadaAfericao());
                }
                if (afericao instanceof AfericaoPlaca) {
                    stmt.setInt(10, pneu.getPosicao());
                } else {
                    stmt.setNull(10, Types.VARCHAR);
                }
                stmt.addBatch();
            }
            executeBatchAndValidate(stmt, EXECUTE_BATCH_SUCCESS, "Erro ao salvar valores da aferição");
        } finally {
            close(stmt);
        }
    }
}