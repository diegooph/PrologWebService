package br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento;

import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.andamento.ColaboradorEmViagem;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.andamento.MarcacaoDentroJornada;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.andamento.ViagemEmAndamento;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.descanso.ColaboradorEmDescanso;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.descanso.ViagemEmDescanso;
import org.jetbrains.annotations.NotNull;

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
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MARCACAO_GET_COLABORADORES_JORNADA_FINALIZADA(?, ?, ?);");
            stmt.setLong(1, codUnidade);
            stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.BIGINT, codCargos));
            stmt.setObject(3, Now.offsetDateTimeUtc());
            rSet = stmt.executeQuery();
            final List<ColaboradorEmDescanso> colaboradores = new ArrayList<>();
            while (rSet.next()) {
                colaboradores.add(new ColaboradorEmDescanso(
                        rSet.getString("NOME_COLABORADOR"),
                        rSet.getObject("DATA_HORA_INICIO_ULTIMA_VIAGEM", LocalDateTime.class),
                        rSet.getObject("DATA_HORA_FIM_ULTIMA_VIAGEM", LocalDateTime.class),
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
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MARCACAO_GET_COLABORADORES_JORNADA_EM_ANDAMENTO(?, ?, ?);");
            stmt.setLong(1, codUnidade);
            stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.BIGINT, codCargos));
            stmt.setObject(3, Now.offsetDateTimeUtc());
            rSet = stmt.executeQuery();
            final List<ColaboradorEmViagem> viagens = new ArrayList<>();
            Long cpfAnterior = null;
            List<MarcacaoDentroJornada> marcacoes = null;
            while (rSet.next()) {
                if (cpfAnterior == null || !cpfAnterior.equals(rSet.getLong("CPF_COLABORADOR"))) {
                    final ColaboradorEmViagem colaboradorEmViagem = new ColaboradorEmViagem(
                            rSet.getString("NOME_COLABORADOR"),
                            rSet.getObject("DATA_HORA_INICIO_JORNADA", LocalDateTime.class),
                            Duration.ofSeconds(rSet.getLong("DURACAO_JORNADA_BRUTA_SEGUNDOS")),
                            Duration.ofSeconds(rSet.getLong("DURACAO_JORNADA_LIQUIDA_SEGUNDOS")),
                            marcacoes = new ArrayList<>(),
                            rSet.getInt("TOTAL_MARCACOES_DENTRO_JORNADA_COLABORADOR") /* :D */,
                            rSet.getBoolean("FOI_AJUSTADO_INICIO_JORNADA"));
                    viagens.add(colaboradorEmViagem);
                }

                marcacoes.add(createMarcacaoDentroJornada(rSet));
                cpfAnterior = rSet.getLong("CPF_COLABORADOR");
            }
            return new ViagemEmAndamento(viagens, viagens.size());
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private MarcacaoDentroJornada createMarcacaoDentroJornada(@NotNull final ResultSet rSet) throws Throwable {
        return new MarcacaoDentroJornada(
                rSet.getLong("COD_TIPO_MARCACAO"),
                rSet.getString("NOME_TIPO_MARCACAO"),
                rSet.getLong("COD_MARCACAO_INICIO"),
                rSet.getLong("COD_MARCACAO_FIM"),
                rSet.getObject("DATA_HORA_INICIO", LocalDateTime.class),
                rSet.getObject("DATA_HORA_FIM", LocalDateTime.class),
                rSet.getBoolean("FOI_AJUSTADO_INICIO"),
                rSet.getBoolean("FOI_AJUSTADO_FIM"),
                rSet.getBoolean("MARCACAO_EM_ANDAMENTO"));
    }
}