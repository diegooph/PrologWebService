package test.gente.controlejornada.relatorios;

import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.ControleJornadaRelatorioService;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.FolhaPontoRelatorio;
import br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.FolhaPontoTipoIntervalo;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import test.BaseTest;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created on 14/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class TotalPorTipoMarcacaoTest extends BaseTest {
    private static final Long COD_UNIDADE = 5L;
    private static final String TODOS_TIPOS_INTERVALOS = "%";
    private static final String DATA_INICIAL = "2018-01-01";
    private static final String DATA_FIM = "2019-01-01";
    private ControleJornadaRelatorioService service;
    private Connection connection;

    @Override
    public void initialize() {
        service = new ControleJornadaRelatorioService();
        connection = DatabaseConnection.getConnection();
    }

    @Override
    public void destroy() {
        DatabaseConnection.close(connection);

    }

    @Test
    public void testTotalPorTipoMarcacaoRelatorio() throws Throwable {
        final Map<String, TotalPorTipoMarcacao> totaisTiposColab = getTotalTempoByTipoIntervaloStmt(
                connection,
                COD_UNIDADE,
                TODOS_TIPOS_INTERVALOS,
                ProLogDateParser.toLocalDate(DATA_INICIAL),
                ProLogDateParser.toLocalDate(DATA_FIM));

        final List<FolhaPontoRelatorio> relatorios = service.getFolhaPontoRelatorio(
                COD_UNIDADE,
                TODOS_TIPOS_INTERVALOS,
                "%",
                DATA_INICIAL,
                DATA_FIM);

        for (final FolhaPontoRelatorio pontoRelatorio : relatorios) {
            for (final FolhaPontoTipoIntervalo tipoMarcacao : pontoRelatorio.getTiposIntervalosMarcados()) {
                final Long cpfColaborador = pontoRelatorio.getColaborador().getCpf();
                final Long codTipoMarcacao = tipoMarcacao.getCodigo();
                final String mapKey = createMapKey(
                        cpfColaborador,
                        codTipoMarcacao);
                final TotalPorTipoMarcacao totalPorTipoMarcacao = totaisTiposColab.remove(mapKey);

                assertNotNull("totalPorTipoMarcacao não encontrado para a chave: " + mapKey, totalPorTipoMarcacao);
                assertEquals(
                        "TOTAL TIPO do colaborador " + cpfColaborador + " e tipo marcação " + codTipoMarcacao,
                        totalPorTipoMarcacao.getTotalTipoMarcacao(),
                        tipoMarcacao.getTempoTotalTipoIntervalo());
                assertEquals(
                        "HORAS NOTURNAS do colaborador " + cpfColaborador + " e tipo marcação " + codTipoMarcacao,
                        totalPorTipoMarcacao.getTotalHorasNoturnasTipoMarcacao(),
                        tipoMarcacao.getTempoTotalHorasNoturnas());
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    @NotNull
    private Map<String, TotalPorTipoMarcacao> getTotalTempoByTipoIntervaloStmt(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final String codTipoIntervalo,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement(
                    "SELECT * FROM FUNC_MARCACAO_GET_TEMPO_TOTAL_POR_TIPO_MARCACAO(?, ?, ?, ?);");
            stmt.setLong(1, codUnidade);
            if (codTipoIntervalo.equals("%")) {
                stmt.setNull(2, Types.BIGINT);
            } else {
                stmt.setLong(2, Long.parseLong(codTipoIntervalo));
            }
            stmt.setObject(3, dataInicial);
            stmt.setObject(4, dataFinal);
            rSet = stmt.executeQuery();
            final Map<String, TotalPorTipoMarcacao> totaisTipoColab = new HashMap<>();
            int totalSize = 0;
            while (rSet.next()) {
                final String chaveMap = createMapKey(
                        rSet.getLong("CPF_COLABORADOR"),
                        rSet.getLong("COD_TIPO_INTERVALO"));
                totaisTipoColab.put(chaveMap, new TotalPorTipoMarcacao(
                        Duration.ofMillis(rSet.getLong("TEMPO_TOTAL_MILLIS")),
                        Duration.ofMillis(rSet.getLong("TEMPO_TOTAL_HORAS_NOTURNAS_MILLIS"))));
                totalSize++;
            }

            // A primeira vista pode parecer meio ridículo comparar isso, pois sempre iteraremos no while e iremos
            // inserir no Map. Porém, se chaves iguais forem criadas pelo método createMapKey, o que não deveria
            // acontecer, algum valor do Map será substituído.
            if (totaisTipoColab.size() != totalSize) {
                throw new IllegalStateException("Map com tamanho diferente do result set, isso pode acontecer se chaves " +
                        "dupliacadas forem criadas, o que denota um erro na busca");
            }

            return totaisTipoColab;
        } finally {
            DatabaseConnection.close(stmt, rSet);
        }
    }

    private void printResultSet(@NotNull final ResultSet resultSet) throws Throwable {
        final ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                String columnValue = resultSet.getString(i);
                System.out.print(columnValue + " " + rsmd.getColumnName(i));
            }
            System.out.println("");
        }
    }

    @NotNull
    private String createMapKey(@NotNull final Long cpfColaborador, @NotNull final Long codTipoMarcacao) {
        return String.valueOf(cpfColaborador).concat(String.valueOf(codTipoMarcacao));
    }

    private final class TotalPorTipoMarcacao {
        @NotNull
        private final Duration totalTipoMarcacao;
        @NotNull
        private final Duration totalHorasNoturnasTipoMarcacao;

        TotalPorTipoMarcacao(@NotNull final Duration totalTipoMarcacao,
                             @NotNull final Duration totalHorasNoturnasTipoMarcacao) {
            this.totalTipoMarcacao = totalTipoMarcacao;
            this.totalHorasNoturnasTipoMarcacao = totalHorasNoturnasTipoMarcacao;
        }

        @NotNull
        Duration getTotalTipoMarcacao() {
            return totalTipoMarcacao;
        }

        @NotNull
        Duration getTotalHorasNoturnasTipoMarcacao() {
            return totalHorasNoturnasTipoMarcacao;
        }
    }
}
