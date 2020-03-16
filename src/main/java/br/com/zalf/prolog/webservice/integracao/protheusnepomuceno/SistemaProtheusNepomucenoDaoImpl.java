package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.AfericaoPlaca;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosAfericaoRealizadaPlaca;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosTipoVeiculoConfiguracaoAfericao;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosUnidadeRestricao;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 12/03/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class SistemaProtheusNepomucenoDaoImpl extends DatabaseConnection implements SistemaProtheusNepomucenoDao {

    @NotNull
    @Override
    public Long insert(@NotNull final Connection conn,
                       @NotNull final Long codUnidade,
                       @NotNull final Afericao afericao) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_PNEU_AFERICAO_INSERT_AFERICAO_INTEGRADA(" +
                    "F_COD_UNIDADE_PROLOG := ?," +
                    "F_CPF_AFERIDOR := ?, " +
                    "F_PLACA_VEICULO := ?, " +
                    "F_COD_TIPO_VEICULO_PROLOG := ?, " +
                    "F_KM_VEICULO := ?, " +
                    "F_TEMPO_REALIZACAO := ?, " +
                    "F_DATA_HORA := ?, " +
                    "F_TIPO_MEDICAO_COLETADA := ?, " +
                    "F_TIPO_PROCESSO_COLETA := ?) AS COD_AFERICAO_INTEGRADA;");
            stmt.setString(1, String.valueOf(codUnidade));
            stmt.setString(2, String.valueOf(afericao.getColaborador().getCpf()));
            stmt.setString(6, String.valueOf(afericao.getTempoRealizacaoAfericaoInMillis()));
            stmt.setString(7, String.valueOf(afericao.getDataHora().atOffset(ZoneOffset.UTC)));
            stmt.setString(8, afericao.getTipoMedicaoColetadaAfericao().asString());
            stmt.setString(9, afericao.getTipoProcessoColetaAfericao().asString());

            if (afericao instanceof AfericaoPlaca) {
                final AfericaoPlaca afericaoPlaca = (AfericaoPlaca) afericao;
                stmt.setString(3, afericaoPlaca.getVeiculo().getPlaca());
                stmt.setString(4, String.valueOf(afericaoPlaca.getVeiculo().getCodTipo()));
                stmt.setString(5, String.valueOf(afericaoPlaca.getKmMomentoAfericao()));
            } else {
                stmt.setNull(3, Types.VARCHAR);
                stmt.setNull(4, Types.VARCHAR);
                stmt.setNull(5, Types.VARCHAR);
            }
            Long codAfericaoIntegrada = null;
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                codAfericaoIntegrada = rSet.getLong("COD_AFERICAO_INTEGRADA");
                afericao.setCodigo(codAfericaoIntegrada);
                insertValores(conn, afericao);
            }
            if (codAfericaoIntegrada != null && codAfericaoIntegrada != 0) {
                return codAfericaoIntegrada;
            } else {
                throw new IllegalStateException("Não foi possível retornar o código da aferição realizada");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    private void insertValores(@NotNull final Connection conn,
                               @NotNull final Afericao afericao) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_PNEU_AFERICAO_INSERT_AFERICAO_VALORES_INTEGRADA(" +
                    "F_COD_AFERICAO_INTEGRADA := ?," +
                    "F_COD_PNEU_PROLOG := ?, " +
                    "F_COD_PNEU_CLIENTE := ?, " +
                    "F_COD_PNEU_CLIENTE_AUXILIAR := ?, " +
                    "F_VIDA_ATUAL := ?, " +
                    "F_PSI := ?, " +
                    "F_ALTURA_SULCO_INTERNO := ?, " +
                    "F_ALTURA_SULCO_CENTRAL_INTERNO := ?, " +
                    "F_ALTURA_SULCO_EXTERNO := ?, " +
                    "F_ALTURA_SULCO_CENTRAL_EXTERNO := ?, " +
                    "F_POSICAO_PROLOG := ?) AS COD_AFERICAO_INTEGRADA;");

            final List<Pneu> pneusAferidos = afericao.getPneusAferidos();
            for (Pneu pneu : pneusAferidos) {
                stmt.setLong(1, afericao.getCodigo());
                // A integração com o sistema da Nepomuceno não mantém pneus no Prolog
                stmt.setNull(2, Types.VARCHAR);
                stmt.setString(3, String.valueOf(pneu.getCodigo()));
                stmt.setString(4, String.valueOf(pneu.getCodigoCliente()));
                stmt.setString(5, String.valueOf(pneu.getVidaAtual()));

                // Já aproveitamos esse switch para atualizar as medições do pneu na tabela PNEU.
                switch (afericao.getTipoMedicaoColetadaAfericao()) {
                    case SULCO_PRESSAO:
                        stmt.setString(6, String.valueOf(pneu.getPressaoAtual()));
                        stmt.setString(7, String.valueOf(pneu.getSulcosAtuais().getInterno()));
                        stmt.setString(8, String.valueOf(pneu.getSulcosAtuais().getCentralInterno()));
                        stmt.setString(9, String.valueOf(pneu.getSulcosAtuais().getExterno()));
                        stmt.setString(10, String.valueOf(pneu.getSulcosAtuais().getCentralExterno()));
                        break;
                    case SULCO:
                        stmt.setNull(6, Types.VARCHAR);
                        stmt.setString(7, String.valueOf(pneu.getSulcosAtuais().getInterno()));
                        stmt.setString(8, String.valueOf(pneu.getSulcosAtuais().getCentralInterno()));
                        stmt.setString(9, String.valueOf(pneu.getSulcosAtuais().getExterno()));
                        stmt.setString(10, String.valueOf(pneu.getSulcosAtuais().getCentralExterno()));
                        break;
                    case PRESSAO:
                        stmt.setString(6, String.valueOf(pneu.getPressaoAtual()));
                        stmt.setNull(7, Types.VARCHAR);
                        stmt.setNull(8, Types.VARCHAR);
                        stmt.setNull(9, Types.VARCHAR);
                        stmt.setNull(10, Types.VARCHAR);
                        break;
                }
                stmt.setString(11, String.valueOf(pneu.getPosicao()));
                if (stmt.executeUpdate() == 0) {
                    throw new SQLException("Não foi possível atualizar as medidas para o pneu: " + pneu.getCodigo());
                }
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public String getCodAuxiliarUnidade(@NotNull final Connection conn,
                                        @NotNull final Long codUnidade) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT COD_AUXILIAR FROM PUBLIC.UNIDADE WHERE CODIGO = ?;");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getString("COD_AUXILIAR");
            }

            throw new SQLException("Não foi possível encontrar o código auxiliar da unidade: " + codUnidade);
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
                    "WHERE COD_UNIDADE = ANY (?);");
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
}
