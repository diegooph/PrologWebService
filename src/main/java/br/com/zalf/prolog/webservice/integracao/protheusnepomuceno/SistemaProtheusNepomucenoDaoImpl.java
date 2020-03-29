package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.StatementUtils;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosAfericaoAvulsa;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosAfericaoRealizadaPlaca;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosTipoVeiculoConfiguracaoAfericao;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosUnidadeRestricao;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            stmt = conn.prepareStatement("select * " +
                    "from integracao.func_pneu_afericao_get_infos_unidade_afericao(f_cod_unidades => ?);");
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
            stmt = conn.prepareStatement("select * " +
                    "from integracao.func_pneu_afericao_get_infos_configuracao_afericao(f_cod_unidades => ?);");
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
            stmt = conn.prepareStatement("select *" +
                    "from integracao.func_pneu_afericao_get_infos_placas_aferidas(f_cod_empresa => ?, " +
                    "                                                             f_placas_afericao => ?, " +
                    "                                                             f_data_hora_atual => ?);");
            stmt.setLong(1, codEmpresa);
            stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.TEXT, placasNepomuceno));
            stmt.setObject(3, Now.localDateTimeUtc());
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
            stmt = conn.prepareStatement("select * " +
                    "from integracao.func_pneu_afericao_get_config_nova_afericao_placa(" +
                    "f_cod_unidade => ?, " +
                    "f_cod_auxiliar_tipo_veiculo => ?);");
            stmt.setLong(1, codUnidade);
            stmt.setString(2, codEstruturaVeiculo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                //noinspection DuplicatedCode
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
            stmt = conn.prepareStatement("select * " +
                    "from integracao.func_pneu_afericao_get_config_nova_afericao_avulsa(f_cod_unidade => ?);");
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
            stmt = conn.prepareStatement("select *" +
                    "from integracao.func_pneu_afericao_get_mapeamento_posicoes_prolog(" +
                    "f_cod_empresa => ?, " +
                    "f_cod_auxiliar_tipo_veiculo => ?);");
            stmt.setLong(1, codEmpresa);
            stmt.setString(2, codEstruturaVeiculo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final BiMap<String, Integer> posicoesPneusProlog = HashBiMap.create();
                do {
                    posicoesPneusProlog.put(
                            rSet.getString("NOMENCLATURA"),
                            rSet.getInt("POSICAO_PROLOG"));
                } while (rSet.next());
                return posicoesPneusProlog;
            } else {
                throw new SQLException("Nenhuma posição mapeada para a estrutura do veículo:\n" +
                        "codEmpresa:" + codEmpresa + "\n" +
                        "codEstruturaVeiculo: " + codEstruturaVeiculo);
            }
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
    public List<String> verificaCodAuxiliarTipoVeiculoValido(@Nullable final Long codEmpresaTipoVeiculo,
                                                             @Nullable final Long codTipoVeiculo) throws Throwable {
        Preconditions.checkArgument(
                codEmpresaTipoVeiculo != null || codTipoVeiculo != null,
                "codEmpresaTipoVeiculo e codTipoVeiculo não pode ser nulos ao mesmo tempo!");
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select vt.cod_auxiliar as cod_auxiliar " +
                    "from veiculo_tipo vt " +
                    "where vt.cod_auxiliar is not null " +
                    "and f_if(? is null, true, vt.cod_empresa = ?) " +
                    "and f_if(? is null, true, vt.codigo != ?);");
            StatementUtils.bindValueOrNull(stmt, 1, codEmpresaTipoVeiculo, SqlType.BIGINT);
            StatementUtils.bindValueOrNull(stmt, 2, codEmpresaTipoVeiculo, SqlType.BIGINT);
            StatementUtils.bindValueOrNull(stmt, 3, codTipoVeiculo, SqlType.BIGINT);
            StatementUtils.bindValueOrNull(stmt, 4, codTipoVeiculo, SqlType.BIGINT);
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