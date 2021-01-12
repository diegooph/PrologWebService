package br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.commons.util.NullIf;
import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.database.StatementUtils;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.andamento.ColaboradorEmViagem;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.andamento.MarcacaoDentroJornada;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.andamento.ViagemEmAndamento;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.descanso.ColaboradorEmDescanso;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.descanso.ViagemEmDescanso;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.Localizacao;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.TipoInicioFim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 31/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AcompanhamentoViagemDaoImpl extends DatabaseConnection implements AcompanhamentoViagemDao {

    @NotNull
    @Override
    public ViagemEmDescanso getColaboradoresEmDescanso(@NotNull final Long codUnidade,
                                                       @NotNull final List<Long> codCargos) throws Throwable {
        PreparedStatement stmt = null;
        Connection conn = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MARCACAO_GET_COLABORADORES_EM_DESCANSO(?, ?, ?);");
            stmt.setLong(1, codUnidade);
            stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.BIGINT, codCargos));
            stmt.setObject(3, Now.getOffsetDateTimeUtc());
            rSet = stmt.executeQuery();
            final List<ColaboradorEmDescanso> colaboradores = new ArrayList<>();
            while (rSet.next()) {
                final long duracaoUltimaViagem = rSet.getLong("DURACAO_ULTIMA_VIAGEM");
                final boolean temDuracaoUltimaViagem = !rSet.wasNull();

                colaboradores.add(new ColaboradorEmDescanso(
                        rSet.getString("NOME_COLABORADOR"),
                        rSet.getObject("DATA_HORA_INICIO_ULTIMA_VIAGEM", LocalDateTime.class),
                        rSet.getObject("DATA_HORA_FIM_ULTIMA_VIAGEM", LocalDateTime.class),
                        temDuracaoUltimaViagem ? Duration.ofSeconds(duracaoUltimaViagem) : null,
                        Duration.ofSeconds(rSet.getLong("TEMPO_DESCANSO_SEGUNDOS")),
                        rSet.getBoolean("FOI_AJUSTADO_INICIO"),
                        rSet.getBoolean("FOI_AJUSTADO_FIM")));
            }
            return new ViagemEmDescanso(colaboradores, colaboradores.size());
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public ViagemEmAndamento getViagensEmAndamento(@NotNull final Long codUnidade,
                                                   @NotNull final List<Long> codCargos) throws Throwable {
        PreparedStatement stmt = null;
        Connection conn = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MARCACAO_GET_COLABORADORES_JORNADA_EM_ANDAMENTO(?, ?, ?)" +
                    ";");
            stmt.setLong(1, codUnidade);
            stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.BIGINT, codCargos));
            stmt.setObject(3, Now.getOffsetDateTimeUtc());
            rSet = stmt.executeQuery();
            final List<ColaboradorEmViagem> viagens = new ArrayList<>();
            Long cpfAnterior = null;
            List<MarcacaoDentroJornada> marcacoes = null;
            while (rSet.next()) {
                final Long cpfAtual = rSet.getLong("CPF_COLABORADOR");
                if (cpfAnterior == null || !cpfAnterior.equals(cpfAtual)) {
                    final ColaboradorEmViagem colaboradorEmViagem = new ColaboradorEmViagem(
                            cpfAtual,
                            rSet.getString("NOME_COLABORADOR"),
                            rSet.getObject("DATA_HORA_INICIO_JORNADA", LocalDateTime.class),
                            Duration.ofSeconds(rSet.getLong("DURACAO_JORNADA_BRUTA_SEGUNDOS")),
                            Duration.ofSeconds(rSet.getLong("DURACAO_JORNADA_LIQUIDA_SEGUNDOS")),
                            marcacoes = new ArrayList<>(),
                            rSet.getInt("TOTAL_MARCACOES_DENTRO_JORNADA_COLABORADOR") /* :D */,
                            rSet.getBoolean("FOI_AJUSTADO_INICIO_JORNADA"));
                    viagens.add(colaboradorEmViagem);
                }

                // O if abaixo é necessário para os casos onde uma jornada não possua nenhuma marcação.
                if (rSet.getLong("COD_TIPO_MARCACAO") > 0) {
                    marcacoes.add(createMarcacaoDentroJornada(rSet));
                }
                cpfAnterior = cpfAtual;
            }
            return new ViagemEmAndamento(viagens, viagens.size());
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public MarcacaoAgrupadaAcompanhamento getMarcacaoInicioFim(@NotNull final Long codUnidade,
                                                               @Nullable final Long codInicio,
                                                               @Nullable final Long codFim) throws Throwable {
        PreparedStatement stmt = null;
        Connection conn = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MARCACAO_GET_MARCACOES_ACOMPANHAMENTO(?, ?, ?);");
            StatementUtils.bindValueOrNull(stmt, 1, codInicio, SqlType.BIGINT);
            StatementUtils.bindValueOrNull(stmt, 2, codFim, SqlType.BIGINT);
            stmt.setString(3, TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                if (!rSet.isLast()) {
                    throw new IllegalStateException("O ResultSet deveria retornar apenas uma linha!");
                }

                return createMarcacaoAgrupada(rSet);
            } else {
                throw new IllegalStateException();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private MarcacaoAgrupadaAcompanhamento createMarcacaoAgrupada(@NotNull final ResultSet rSet) throws Throwable {
        return new MarcacaoAgrupadaAcompanhamento(
                rSet.getString("NOME_TIPO_MARCACAO"),
                rSet.getString("CPF_COLABORADOR"),
                rSet.getString("NOME_COLABORADOR"),
                rSet.getLong("COD_MARCACAO_INICIO") > 0 ? createMarcacaoInicio(rSet) : null,
                rSet.getLong("COD_MARCACAO_FIM") > 0 ? createMarcacaoFim(rSet) : null,
                Duration.ofSeconds(rSet.getLong("TEMPO_DECORRIDO_ENTRE_INICIO_FIM_SEGUNDOS")),
                Duration.ofSeconds(rSet.getLong("TEMPO_RECOMENDADO_TIPO_MARCACAO_SEGUNDOS")),
                rSet.getString("JUSTIFICATIVA_ESTOURO"),
                rSet.getString("JUSTIFICATIVA_TEMPO_RECOMENDADO"));
    }

    @NotNull
    private MarcacaoAcompanhamento createMarcacaoInicio(@NotNull final ResultSet rSet) throws Throwable {
        return new MarcacaoAcompanhamento(
                rSet.getLong("COD_MARCACAO_INICIO"),
                rSet.getObject("DATA_HORA_INICIO", LocalDateTime.class),
                FonteDataHora.fromString(rSet.getString("FONTE_DATA_HORA_INICIO")),
                TipoInicioFim.MARCACAO_INICIO,
                rSet.getString("LATITUDE_MARCACAO_INICIO") != null
                        ? new Localizacao(
                        rSet.getString("LATITUDE_MARCACAO_INICIO"),
                        rSet.getString("LONGITUDE_MARCACAO_INICIO"))
                        : null,
                NullIf.equalOrLess(rSet.getInt("VERSAO_APP_MOMENTO_MARCACAO_INICIO"), 0),
                NullIf.equalOrLess(rSet.getInt("VERSAO_APP_MOMENTO_SINCRONIZACAO_INICIO"), 0),
                rSet.getBoolean("FOI_AJUSTADO_INICIO"),
                rSet.getString("DEVICE_IMEI_INICIO"),
                rSet.getBoolean("DEVICE_IMEI_INICIO_RECONHECIDO"),
                rSet.getString("DEVICE_MARCA_INICIO"),
                rSet.getString("DEVICE_MODELO_INICIO"));
    }

    @NotNull
    private MarcacaoAcompanhamento createMarcacaoFim(@NotNull final ResultSet rSet) throws Throwable {
        return new MarcacaoAcompanhamento(
                rSet.getLong("COD_MARCACAO_FIM"),
                rSet.getObject("DATA_HORA_FIM", LocalDateTime.class),
                FonteDataHora.fromString(rSet.getString("FONTE_DATA_HORA_FIM")),
                TipoInicioFim.MARCACAO_FIM,
                rSet.getString("LATITUDE_MARCACAO_FIM") != null
                        ? new Localizacao(
                        rSet.getString("LATITUDE_MARCACAO_FIM"),
                        rSet.getString("LONGITUDE_MARCACAO_FIM"))
                        : null,
                NullIf.equalOrLess(rSet.getInt("VERSAO_APP_MOMENTO_MARCACAO_FIM"), 0),
                NullIf.equalOrLess(rSet.getInt("VERSAO_APP_MOMENTO_SINCRONIZACAO_FIM"), 0),
                rSet.getBoolean("FOI_AJUSTADO_FIM"),
                rSet.getString("DEVICE_IMEI_FIM"),
                rSet.getBoolean("DEVICE_IMEI_FIM_RECONHECIDO"),
                rSet.getString("DEVICE_MARCA_FIM"),
                rSet.getString("DEVICE_MODELO_FIM"));
    }

    @NotNull
    private MarcacaoDentroJornada createMarcacaoDentroJornada(@NotNull final ResultSet rSet) throws Throwable {
        return new MarcacaoDentroJornada(
                rSet.getLong("COD_TIPO_MARCACAO"),
                rSet.getString("NOME_TIPO_MARCACAO"),
                NullIf.equalOrLess(rSet.getLong("COD_MARCACAO_INICIO"), 0),
                NullIf.equalOrLess(rSet.getLong("COD_MARCACAO_FIM"), 0),
                rSet.getObject("DATA_HORA_INICIO", LocalDateTime.class),
                rSet.getObject("DATA_HORA_FIM", LocalDateTime.class),
                rSet.getBoolean("FOI_AJUSTADO_INICIO"),
                rSet.getBoolean("FOI_AJUSTADO_FIM"),
                rSet.getBoolean("MARCACAO_EM_ANDAMENTO"));
    }
}