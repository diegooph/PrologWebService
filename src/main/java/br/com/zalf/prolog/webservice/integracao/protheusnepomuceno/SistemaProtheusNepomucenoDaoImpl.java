package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.database.StatementUtils;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.AfericaoPlaca;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.ConfiguracaoNovaAfericaoAvulsa;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.zalf.prolog.webservice.commons.util.database.StatementUtils.executeBatchAndValidate;

/**
 * Created on 12/03/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class SistemaProtheusNepomucenoDaoImpl extends DatabaseConnection implements SistemaProtheusNepomucenoDao {

    private static final int EXECUTE_BATCH_SUCCESS = 0;

    @NotNull
    @Override
    public List<Long> getApenasUnidadesMapeadas(@NotNull final Connection conn,
                                                @NotNull final List<Long> codUnidades) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT U.CODIGO AS COD_UNIDADE " +
                    "FROM UNIDADE U " +
                    "WHERE U.COD_AUXILIAR IS NOT NULL " +
                    "AND U.CODIGO = ANY (?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            rSet = stmt.executeQuery();
            final List<Long> codUnidadesMapeadas = new ArrayList<>();
            if (rSet.next()) {
                do {
                    codUnidadesMapeadas.add(rSet.getLong("COD_UNIDADE"));
                } while (rSet.next());
            }
            return codUnidadesMapeadas;
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Long insert(@NotNull final Connection conn,
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
    public Map<Long, String> getCodFiliais(@NotNull final Connection conn,
                                           @NotNull final List<Long> codUnidades) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("select * " +
                    "from integracao.func_pneu_afericao_get_cod_auxiliar_unidade_prolog(f_cod_unidades => ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final Map<Long, String> codUnidadePrologCodAuxiliar = new HashMap<>();
                do {
                    codUnidadePrologCodAuxiliar.put(
                            rSet.getLong("COD_UNIDADE_PROLOG"),
                            rSet.getString("COD_AUXILIAR"));
                } while (rSet.next());
                return codUnidadePrologCodAuxiliar;
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
            // Executamos as informações em lote, já que o processo não tem dependência entre as linhas.
            executeBatchAndValidate(stmt, EXECUTE_BATCH_SUCCESS, "Erro ao salvar valores da aferiçãpo");
        } finally {
            close(stmt);
        }
    }
}