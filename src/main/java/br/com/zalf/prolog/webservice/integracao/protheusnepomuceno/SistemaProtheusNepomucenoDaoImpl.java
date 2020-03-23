package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosAfericaoAvulsa;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosAfericaoRealizadaPlaca;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosTipoVeiculoConfiguracaoAfericao;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosUnidadeRestricao;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.zalf.prolog.webservice.commons.util.StatementUtils.executeBatchAndValidate;

/**
 * Created on 12/03/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class SistemaProtheusNepomucenoDaoImpl extends DatabaseConnection implements SistemaProtheusNepomucenoDao {
    private static final int EXECUTE_BATCH_SUCCESS = 0;

    @NotNull
    @Override
    public Long insert(@NotNull final Connection conn,
                       @NotNull final Long codUnidade,
                       @NotNull final Afericao afericao) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_PNEU_AFERICAO_INSERT_AFERICAO_INTEGRADA(" +
                    "F_COD_UNIDADE_PROLOG => ?," +
                    "F_CPF_AFERIDOR => ?, " +
                    "F_PLACA_VEICULO => ?::TEXT, " +
                    "F_COD_AUXILIAR_TIPO_VEICULO_PROLOG => ?, " +
                    "F_KM_VEICULO => ?::TEXT, " +
                    "F_TEMPO_REALIZACAO => ?, " +
                    "F_DATA_HORA => ?, " +
                    "F_TIPO_MEDICAO_COLETADA => ?, " +
                    "F_TIPO_PROCESSO_COLETA => ?) AS COD_AFERICAO_INTEGRADA;");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, String.valueOf(afericao.getColaborador().getCpf()));
            if (afericao instanceof AfericaoPlaca) {
                final AfericaoPlaca afericaoPlaca = (AfericaoPlaca) afericao;
                stmt.setString(3, afericaoPlaca.getVeiculo().getPlaca());
                // Setamos o código auxiliar do tipo no nome do diagrama.
                stmt.setString(4, afericaoPlaca.getVeiculo().getDiagrama().getNome());
                stmt.setString(5, String.valueOf(afericaoPlaca.getKmMomentoAfericao()));
            } else {
                stmt.setNull(3, Types.VARCHAR);
                stmt.setNull(4, Types.VARCHAR);
                stmt.setNull(5, Types.VARCHAR);
            }
            stmt.setLong(6, afericao.getTempoRealizacaoAfericaoInMillis());
            stmt.setObject(7, afericao.getDataHora().atOffset(ZoneOffset.UTC));
            stmt.setString(8, afericao.getTipoMedicaoColetadaAfericao().asString());
            stmt.setString(9, afericao.getTipoProcessoColetaAfericao().asString());
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
    public List<InfosAfericaoAvulsa> getInfosAfericaoAvulsa(@NotNull final Connection conn,
                                                            @NotNull final Long codUnidade,
                                                            @NotNull final List<String> codPneus) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement(
                    "SELECT * FROM INTEGRACAO.FUNC_PNEU_AFERICAO_GET_INFOS_AFERICOES_INTEGRADA(" +
                            "F_COD_UNIDADE => ?," +
                            "F_COD_PNEUS_CLIENTE => ?);");
            stmt.setLong(1, codUnidade);
            stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.TEXT, codPneus));
            rSet = stmt.executeQuery();
            final List<InfosAfericaoAvulsa> infosAfericaoAvulsa = new ArrayList<>();
            while (rSet.next()) {
                infosAfericaoAvulsa.add(createInfosAfericaoAvulsa(rSet));
            }
            return infosAfericaoAvulsa;
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Map<String, InfosUnidadeRestricao> getInfosUnidadeRestricao(
            @NotNull final Connection conn,
            @NotNull final List<Long> codUnidades) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT U.COD_AUXILIAR AS COD_AUXILIAR, " +
                    "PRU.COD_UNIDADE AS COD_UNIDADE, " +
                    "PRU.PERIODO_AFERICAO_SULCO AS PERIODO_DIAS_AFERICAO_SULCO, " +
                    "PRU.PERIODO_AFERICAO_PRESSAO AS PERIODO_DIAS_AFERICAO_PRESSAO " +
                    "FROM PNEU_RESTRICAO_UNIDADE PRU " +
                    "         JOIN UNIDADE U ON PRU.COD_UNIDADE = U.CODIGO " +
                    "WHERE COD_UNIDADE = ANY (?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            rSet = stmt.executeQuery();
            final Map<String, InfosUnidadeRestricao> infosUnidadeRestricao = new HashMap<>();
            if (rSet.next()) {
                do {
                    infosUnidadeRestricao.put(
                            rSet.getString("COD_AUXILIAR"),
                            new InfosUnidadeRestricao(
                                    rSet.getLong("COD_UNIDADE"),
                                    rSet.getInt("PERIODO_DIAS_AFERICAO_SULCO"),
                                    rSet.getInt("PERIODO_DIAS_AFERICAO_PRESSAO")));
                } while (rSet.next());
            } else {
                throw new SQLException("Nenhuma informação de restrição de unidade encontrarada para as unidades:\n" +
                        "codUnidades: " + codUnidades.toString());
            }
            return infosUnidadeRestricao;
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Map<String, InfosTipoVeiculoConfiguracaoAfericao> getInfosTipoVeiculoConfiguracaoAfericao(
            @NotNull final Connection conn,
            @NotNull final List<Long> codUnidades) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT VT.COD_AUXILIAR                 AS COD_AUXILIAR, " +
                    "ACTAV.COD_UNIDADE               AS COD_UNIDADE, " +
                    "ACTAV.COD_TIPO_VEICULO          AS COD_TIPO_VEICULO, " +
                    "ACTAV.PODE_AFERIR_SULCO         AS PODE_AFERIR_SULCO, " +
                    "ACTAV.PODE_AFERIR_PRESSAO       AS PODE_AFERIR_PRESSAO, " +
                    "ACTAV.PODE_AFERIR_SULCO_PRESSAO AS PODE_AFERIR_SULCO_PRESSAO, " +
                    "ACTAV.PODE_AFERIR_ESTEPE        AS PODE_AFERIR_ESTEPE " +
                    "FROM AFERICAO_CONFIGURACAO_TIPO_AFERICAO_VEICULO ACTAV " +
                    "         JOIN VEICULO_TIPO VT ON ACTAV.COD_TIPO_VEICULO = VT.CODIGO " +
                    "WHERE COD_UNIDADE = ANY (?) AND VT.COD_AUXILIAR IS NOT NULL;");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            rSet = stmt.executeQuery();
            final Map<String, InfosTipoVeiculoConfiguracaoAfericao> tipoVeiculoConfiguracao = new HashMap<>();
            if (rSet.next()) {
                do {
                    tipoVeiculoConfiguracao.put(
                            rSet.getString("COD_AUXILIAR"),
                            new InfosTipoVeiculoConfiguracaoAfericao(
                                    rSet.getLong("COD_UNIDADE"),
                                    rSet.getLong("COD_TIPO_VEICULO"),
                                    rSet.getBoolean("PODE_AFERIR_SULCO"),
                                    rSet.getBoolean("PODE_AFERIR_PRESSAO"),
                                    rSet.getBoolean("PODE_AFERIR_SULCO_PRESSAO"),
                                    rSet.getBoolean("PODE_AFERIR_ESTEPE")));
                } while (rSet.next());
            } else {
                throw new SQLException("Nenhuma configuração de tipo de veículo encontrarada para as unidades:\n" +
                        "codUnidades: " + codUnidades.toString());
            }
            return tipoVeiculoConfiguracao;
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Map<String, InfosAfericaoRealizadaPlaca> getInfosAfericaoRealizadaPlaca(
            @NotNull final Connection conn,
            @NotNull final Long codEmpresa,
            @NotNull final List<String> placasNepomuceno) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("WITH PLACAS AS (SELECT UNNEST(?) AS PLACA)\n" +
                    "SELECT P.PLACA,\n" +
                    "       COALESCE(INTERVALO_PRESSAO.INTERVALO, -1) :: INTEGER AS INTERVALO_PRESSAO,\n" +
                    "       COALESCE(INTERVALO_SULCO.INTERVALO, -1) :: INTEGER   AS INTERVALO_SULCO\n" +
                    "FROM PLACAS P\n" +
                    "         LEFT JOIN INTEGRACAO.AFERICAO_INTEGRADA AI\n" +
                    "                   ON P.PLACA = AI.PLACA_VEICULO AND AI.COD_EMPRESA_PROLOG::BIGINT = ?\n" +
                    "         LEFT JOIN (SELECT AI.PLACA_VEICULO                                                  AS PLACA_INTERVALO,\n" +
                    "                           EXTRACT(DAYS FROM (NOW()) -\n" +
                    "                                             MAX(AI.DATA_HORA::TIMESTAMP WITH TIME ZONE AT TIME ZONE\n" +
                    "                                                 TZ_UNIDADE(AI.COD_UNIDADE_PROLOG::BIGINT))) AS INTERVALO\n" +
                    "                    FROM INTEGRACAO.AFERICAO_INTEGRADA AI\n" +
                    "                    WHERE AI.TIPO_MEDICAO_COLETADA = 'PRESSAO'\n" +
                    "                       OR AI.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'\n" +
                    "                    GROUP BY AI.PLACA_VEICULO) AS INTERVALO_PRESSAO\n" +
                    "                   ON INTERVALO_PRESSAO.PLACA_INTERVALO = P.PLACA\n" +
                    "         LEFT JOIN (SELECT AI.PLACA_VEICULO                                                  AS PLACA_INTERVALO,\n" +
                    "                           EXTRACT(DAYS FROM (NOW()) -\n" +
                    "                                             MAX(AI.DATA_HORA::TIMESTAMP WITH TIME ZONE AT TIME ZONE\n" +
                    "                                                 TZ_UNIDADE(AI.COD_UNIDADE_PROLOG::BIGINT))) AS INTERVALO\n" +
                    "                    FROM INTEGRACAO.AFERICAO_INTEGRADA AI\n" +
                    "                    WHERE AI.TIPO_MEDICAO_COLETADA = 'SULCO'\n" +
                    "                       OR AI.TIPO_MEDICAO_COLETADA = 'SULCO_PRESSAO'\n" +
                    "                    GROUP BY AI.PLACA_VEICULO) AS INTERVALO_SULCO\n" +
                    "                   ON INTERVALO_SULCO.PLACA_INTERVALO = P.PLACA;");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.TEXT, placasNepomuceno));
            stmt.setLong(2, codEmpresa);
            rSet = stmt.executeQuery();
            final Map<String, InfosAfericaoRealizadaPlaca> afericaoRealizadaPlaca = new HashMap<>();
            if (rSet.next()) {
                do {
                    afericaoRealizadaPlaca.put(
                            rSet.getString("PLACA"),
                            new InfosAfericaoRealizadaPlaca(
                                    rSet.getString("PLACA"),
                                    rSet.getInt("INTERVALO_SULCO"),
                                    rSet.getInt("INTERVALO_PRESSAO")
                            ));
                } while (rSet.next());
            } else {
                throw new SQLException("Nenhuma informação de aferição encontrada para as placas:\n" +
                        "placasNepomuceno: " + placasNepomuceno.toString());
            }
            return afericaoRealizadaPlaca;
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
            stmt = conn.prepareStatement("SELECT PRU.SULCO_MINIMO_DESCARTE, " +
                    "       PRU.SULCO_MINIMO_RECAPAGEM, " +
                    "       PRU.TOLERANCIA_INSPECAO, " +
                    "       PRU.TOLERANCIA_CALIBRAGEM, " +
                    "       PRU.PERIODO_AFERICAO_SULCO, " +
                    "       PRU.PERIODO_AFERICAO_PRESSAO, " +
                    "       CONFIG_PODE_AFERIR.PODE_AFERIR_SULCO, " +
                    "       CONFIG_PODE_AFERIR.PODE_AFERIR_PRESSAO, " +
                    "       CONFIG_PODE_AFERIR.PODE_AFERIR_SULCO_PRESSAO, " +
                    "       CONFIG_PODE_AFERIR.PODE_AFERIR_ESTEPE, " +
                    "       CONFIG_ALERTA_SULCO.VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS, " +
                    "       CONFIG_ALERTA_SULCO.VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS, " +
                    "       CONFIG_ALERTA_SULCO.BLOQUEAR_VALORES_MENORES, " +
                    "       CONFIG_ALERTA_SULCO.BLOQUEAR_VALORES_MAIORES, " +
                    "       CONFIG_ALERTA_SULCO.USA_DEFAULT_PROLOG AS VARIACOES_SULCO_DEFAULT_PROLOG " +
                    "FROM FUNC_AFERICAO_GET_CONFIG_TIPO_AFERICAO_VEICULO(?) AS CONFIG_PODE_AFERIR " +
                    "         JOIN VIEW_AFERICAO_CONFIGURACAO_ALERTA_SULCO AS CONFIG_ALERTA_SULCO " +
                    "              ON CONFIG_PODE_AFERIR.COD_UNIDADE_CONFIGURACAO = CONFIG_ALERTA_SULCO.COD_UNIDADE " +
                    "         JOIN PNEU_RESTRICAO_UNIDADE PRU " +
                    "              ON PRU.COD_UNIDADE = CONFIG_PODE_AFERIR.COD_UNIDADE_CONFIGURACAO " +
                    "         JOIN VEICULO_TIPO VT ON VT.COD_AUXILIAR = ? " +
                    "WHERE CONFIG_PODE_AFERIR.COD_UNIDADE_CONFIGURACAO = ? " +
                    "  AND CONFIG_PODE_AFERIR.COD_TIPO_VEICULO = VT.CODIGO;");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, codEstruturaVeiculo);
            stmt.setLong(3, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return new ConfiguracaoNovaAfericaoPlaca(
                        rSet.getBoolean("PODE_AFERIR_SULCO"),
                        rSet.getBoolean("PODE_AFERIR_PRESSAO"),
                        rSet.getBoolean("PODE_AFERIR_SULCO_PRESSAO"),
                        rSet.getBoolean("PODE_AFERIR_ESTEPE"),
                        rSet.getDouble("SULCO_MINIMO_DESCARTE"),
                        rSet.getDouble("SULCO_MINIMO_RECAPAGEM"),
                        rSet.getDouble("TOLERANCIA_INSPECAO"),
                        rSet.getDouble("TOLERANCIA_CALIBRAGEM"),
                        rSet.getInt("PERIODO_AFERICAO_SULCO"),
                        rSet.getInt("PERIODO_AFERICAO_PRESSAO"),
                        rSet.getDouble("VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS"),
                        rSet.getDouble("VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS"),
                        rSet.getBoolean("VARIACOES_SULCO_DEFAULT_PROLOG"),
                        rSet.getBoolean("BLOQUEAR_VALORES_MENORES"),
                        rSet.getBoolean("BLOQUEAR_VALORES_MAIORES"));
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
                    "SELECT PRU.SULCO_MINIMO_DESCARTE                       AS SULCO_MINIMO_DESCARTE, " +
                            "PRU.SULCO_MINIMO_RECAPAGEM                                 AS SULCO_MINIMO_RECAPAGEM, " +
                            "PRU.TOLERANCIA_INSPECAO                                    AS TOLERANCIA_INSPECAO, " +
                            "PRU.TOLERANCIA_CALIBRAGEM                                  AS TOLERANCIA_CALIBRAGEM, " +
                            "PRU.PERIODO_AFERICAO_SULCO                                 AS PERIODO_AFERICAO_SULCO, " +
                            "PRU.PERIODO_AFERICAO_PRESSAO                               AS PERIODO_AFERICAO_PRESSAO, " +
                            "CONFIG_ALERTA_SULCO.VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS AS VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS, " +
                            "CONFIG_ALERTA_SULCO.VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS AS VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS, " +
                            "CONFIG_ALERTA_SULCO.BLOQUEAR_VALORES_MENORES               AS BLOQUEAR_VALORES_MENORES, " +
                            "CONFIG_ALERTA_SULCO.BLOQUEAR_VALORES_MAIORES               AS BLOQUEAR_VALORES_MAIORES, " +
                            "CONFIG_ALERTA_SULCO.USA_DEFAULT_PROLOG                     AS VARIACOES_SULCO_DEFAULT_PROLOG " +
                            "FROM VIEW_AFERICAO_CONFIGURACAO_ALERTA_SULCO CONFIG_ALERTA_SULCO " +
                            "   JOIN PNEU_RESTRICAO_UNIDADE PRU " +
                            "       ON PRU.COD_UNIDADE = CONFIG_ALERTA_SULCO.COD_UNIDADE " +
                            "WHERE CONFIG_ALERTA_SULCO.COD_UNIDADE = ?;");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return new ConfiguracaoNovaAfericaoAvulsa(
                        rSet.getDouble("SULCO_MINIMO_DESCARTE"),
                        rSet.getDouble("SULCO_MINIMO_RECAPAGEM"),
                        rSet.getDouble("TOLERANCIA_INSPECAO"),
                        rSet.getDouble("TOLERANCIA_CALIBRAGEM"),
                        rSet.getInt("PERIODO_AFERICAO_SULCO"),
                        rSet.getInt("PERIODO_AFERICAO_PRESSAO"),
                        rSet.getDouble("VARIACAO_ACEITA_SULCO_MENOR_MILIMETROS"),
                        rSet.getDouble("VARIACAO_ACEITA_SULCO_MAIOR_MILIMETROS"),
                        rSet.getBoolean("VARIACOES_SULCO_DEFAULT_PROLOG"),
                        rSet.getBoolean("BLOQUEAR_VALORES_MENORES"),
                        rSet.getBoolean("BLOQUEAR_VALORES_MAIORES"));
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
    public BiMap<String, Integer> getMapeamentoPosicoesProlog(
            @NotNull final Connection conn,
            @NotNull final Long codEmpresa,
            @NotNull final String codEstruturaVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT " +
                    "       PPNE.POSICAO_PROLOG AS POSICAO_PROLOG, " +
                    "       PPNE.NOMENCLATURA AS NOMENCLATURA " +
                    "FROM VEICULO_TIPO VT " +
                    "JOIN PNEU_POSICAO_NOMENCLATURA_EMPRESA PPNE ON VT.COD_DIAGRAMA = PPNE.COD_DIAGRAMA " +
                    "WHERE VT.COD_AUXILIAR = ? AND PPNE.COD_EMPRESA = ?;");
            stmt.setString(1, codEstruturaVeiculo);
            stmt.setLong(2, codEmpresa);
            rSet = stmt.executeQuery();
            final BiMap<String, Integer> posicoesPneusProlog = HashBiMap.create();
            if (rSet.next()) {
                do {
                    posicoesPneusProlog.put(
                            rSet.getString("NOMENCLATURA"),
                            rSet.getInt("POSICAO_PROLOG"));
                } while (rSet.next());
            } else {
                throw new SQLException("Nenhuma posição mapeada para a estrutura do veículo:\n" +
                        "codEmpresa:" + codEmpresa + "\n" +
                        "codEstruturaVeiculo: " + codEstruturaVeiculo);
            }
            return posicoesPneusProlog;
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Short getCodDiagramaByCodEstrutura(
            @NotNull final Connection conn,
            @NotNull final Long codEmpresa,
            @NotNull final String codEstruturaVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT VT.COD_DIAGRAMA AS COD_DIAGRAMA " +
                    "FROM VEICULO_TIPO VT " +
                    "WHERE VT.COD_AUXILIAR = ? " +
                    "  AND VT.COD_EMPRESA = ?;");
            stmt.setString(1, codEstruturaVeiculo);
            stmt.setLong(2, codEmpresa);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getShort("COD_DIAGRAMA");
            } else {
                throw new SQLException("Nenhum diagrama encontrado para a estrutura do veículo:\n" +
                        "codEmpresa:" + codEmpresa + "\n" +
                        "codEstruturaVeiculo: " + codEstruturaVeiculo);
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public String getCodFiliais(@NotNull final Connection conn,
                                @NotNull final List<Long> codUnidades) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT STRING_AGG(COD_AUXILIAR, '_') AS COD_AUXILIAR " +
                    "FROM PUBLIC.UNIDADE WHERE CODIGO = ANY(?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getString("COD_AUXILIAR");
            } else {
                throw new SQLException("Nenhum código de filial mapeado para as unidades:\n" +
                        "codUnidades: " + codUnidades.toString());
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<String> verificaCodAuxiliarTipoVeiculoValido(@NotNull final Long codTipoVeiculo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select vt.cod_auxiliar as cod_auxiliar " +
                    "from veiculo_tipo vt " +
                    "where vt.codigo != ? " +
                    "and vt.cod_empresa = (select cod_empresa from veiculo_tipo where codigo = ?) " +
                    "and vt.cod_auxiliar is not null;");
            stmt.setLong(1, codTipoVeiculo);
            stmt.setLong(2, codTipoVeiculo);
            rSet = stmt.executeQuery();
            final List<String> codigosAuxiliares = new ArrayList<>();
            while (rSet.next()) {
                codigosAuxiliares.add(rSet.getString("cod_auxiliar"));
            }
            return codigosAuxiliares;
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
            for (Pneu pneu : afericao.getPneusAferidos()) {
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
            // Executamos as informações em lote, já que o processo não tem dependência entre as linhas.
            executeBatchAndValidate(stmt, EXECUTE_BATCH_SUCCESS, "Erro ao salvar valores da aferiçãpo");
        } finally {
            close(stmt);
        }
    }

    @NotNull
    private InfosAfericaoAvulsa createInfosAfericaoAvulsa(@NotNull final ResultSet rSet) throws Throwable {
        return new InfosAfericaoAvulsa(
                rSet.getLong("CODIGO_ULTIMA_AFERICAO"),
                rSet.getString("COD_PNEU"),
                rSet.getString("COD_PNEU_CLIENTE"),
                rSet.getObject("DATA_HORA_ULTIMA_AFERICAO", LocalDateTime.class),
                rSet.getString("NOME_COLABORADOR_AFERICAO"),
                TipoMedicaoColetadaAfericao.fromString(rSet.getString("TIPO_MEDICAO_COLETADA")),
                TipoProcessoColetaAfericao.fromString(rSet.getString("TIPO_PROCESSO_COLETA")),
                rSet.getString("PLACA_APLICADO_QUANDO_AFERIDO")
        );
    }
}