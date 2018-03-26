package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.relatorios;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.CsvWriter;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;

/**
 * Created by luiz on 26/04/17.
 */
public class RelatoriosOrdemServicoDaoImpl extends DatabaseConnection implements RelatoriosOrdemServicoDao {

    @Override
    public void getItensMaiorQuantidadeNokCsv(@NotNull OutputStream outputStream,
                                              @NotNull Long codUnidade,
                                              @NotNull Date dataInicial,
                                              @NotNull Date dataFinal) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getItensMaiorQuantidadeNok(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getItensMaiorQuantidadeNokReport(@NotNull Long codUnidade,
                                                   @NotNull Date dataInicial,
                                                   @NotNull Date dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getItensMaiorQuantidadeNok(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getMediaTempoConsertoItemCsv(@NotNull OutputStream outputStream,
                                             @NotNull Long codUnidade,
                                             @NotNull Date dataInicial,
                                             @NotNull Date dataFinal) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getMediaTempoConsertoItem(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getMediaTempoConsertoItemReport(@NotNull Long codUnidade,
                                                  @NotNull Date dataInicial,
                                                  @NotNull Date dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getMediaTempoConsertoItem(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getProdutividadeMecanicosCsv(@NotNull OutputStream outputStream,
                                             @NotNull Long codUnidade,
                                             @NotNull Date dataInicial,
                                             @NotNull Date dataFinal) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getProdutividadeMecanicos(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getProdutividadeMecanicosReport(@NotNull Long codUnidade,
                                                  @NotNull Date dataInicial,
                                                  @NotNull Date dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getProdutividadeMecanicos(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    private PreparedStatement getItensMaiorQuantidadeNok(Connection conn, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT " +
                "cp.pergunta AS \"PERGUNTA\", " +
                "cap.alternativa AS \"ALTERNATIVA\", " +
                "prioridade AS \"PRIORIDADE\", " +
                "sum(case when cr.resposta <> 'OK' then 1 else 0 end ) as \"TOTAL MARCAÇÕES NOK\", " +
                "count(cp.pergunta) as \"TOTAL REALIZAÇÕES\", " +
                "trunc((sum(case when cr.resposta <> 'OK' then 1 else 0 end ) / count(cp.pergunta)::float) * 100) || '%' as \"PROPORÇÃO\" " +
                "FROM checklist c " +
                "JOIN checklist_respostas cr ON c.cod_unidade = cr.cod_unidade AND cr.cod_checklist_modelo = c.cod_checklist_modelo\n" +
                "JOIN checklist_perguntas cp ON cp.cod_unidade = C.cod_unidade AND cp.codigo = CR.cod_pergunta AND cp.cod_checklist_modelo = cr.cod_checklist_modelo " +
                "JOIN veiculo v ON v.placa::text = c.placa_veiculo::text " +
                "JOIN checklist_alternativa_pergunta cap ON cap.cod_unidade = cp.cod_unidade AND cap.cod_checklist_modelo = cp.cod_checklist_modelo " +
                "AND cap.cod_pergunta = cp.codigo AND cap.codigo = cr.cod_alternativa " +
                "AND cr.cod_checklist = c.codigo AND cr.cod_pergunta = cp.codigo AND cr.cod_alternativa = cap.codigo " +
                "WHERE c.cod_unidade = ? and c.data_hora BETWEEN (? AT TIME ZONE ?) and (? AT TIME ZONE ?) " +
                "GROUP BY 1, 2, 3 " +
                "ORDER BY trunc((sum( case when cr.resposta <> 'OK' then 1 else 0 end ) / count(cp.pergunta)::float) * 100) desc");
        final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId();
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, dataInicial);
        stmt.setString(3, zoneId);
        stmt.setDate(4, dataFinal);
        stmt.setString(5, zoneId);
        return stmt;
    }

    @NotNull
    private PreparedStatement getMediaTempoConsertoItem(Connection conn, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT " +
                "pergunta, " +
                "alternativa, " +
                "prioridade, " +
                "prazo_conserto_em_horas AS \"PRAZO CONSERTO EM HORAS\"," +
                "qt_apontados AS \"QT APONTADOS\", " +
                "qt_resolvidos_dentro_prazo AS \"QT RESOLVIDOS DENTRO PRAZO\", " +
                "trunc(md_tempo_conserto_segundos/3600) || ' / ' || " +
                "trunc(md_tempo_conserto_segundos/60) as \"MD TEMPO CONSERTO HORAS/MINUTOS\", " +
                "round((qt_resolvidos_dentro_prazo/qt_apontados::float)*100) || '%' as \"PORCENTAGEM\" " +
                "FROM " +
                "   (SELECT pergunta, " +
                "   alternativa, " +
                "   prioridade, " +
                "   prazo as prazo_conserto_em_horas, " +
                "   count(pergunta) as qt_apontados, " +
                "   sum(case when (extract(epoch from (data_hora_conserto - data_hora)) / 3600) <= prazo then 1 else 0 end) as " +
                "   qt_resolvidos_dentro_prazo, " +
                "   trunc(extract(epoch from avg(data_hora_conserto - " +
                "   estratificacao_os.data_hora))) as md_tempo_conserto_segundos " +
                "FROM estratificacao_os " +
                "WHERE cod_unidade = ? AND data_hora BETWEEN (? AT TIME ZONE ?) AND (? AT TIME ZONE ?) " +
                "GROUP BY 1, 2, 3, 4) as dados " +
                "ORDER BY round((qt_resolvidos_dentro_prazo / qt_apontados::float) * 100) " +
                "desc;");
        final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId();
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, dataInicial);
        stmt.setString(3, zoneId);
        stmt.setDate(4, dataFinal);
        stmt.setString(5, zoneId);
        return stmt;
    }

    @NotNull
    private PreparedStatement getProdutividadeMecanicos(Connection conn, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT " +
                "nome_mecanico AS \"MECÂNICO\", " +
                "count(nome_mecanico) as \"CONSERTOS\", " +
                "sum(tempo_realizacao/3600000) as \"HORAS\", " +
                "round(avg(tempo_realizacao/3600000)) as \"HORAS POR CONSERTO\" " +
                "FROM estratificacao_os " +
                "WHERE tempo_realizacao is not null and tempo_realizacao > 0 and " +
                "cod_unidade = ? and data_hora BETWEEN (? AT TIME ZONE ?) AND (? AT TIME ZONE ?) " +
                "GROUP BY 1 " +
                "ORDER BY nome_mecanico;");
        final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId();
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, dataInicial);
        stmt.setString(3, zoneId);
        stmt.setDate(4, dataFinal);
        stmt.setString(5, zoneId);
        return stmt;
    }

    @NotNull
    private PreparedStatement getEstratificacaoOs(Connection conn, Long codUnidade, String placa, Date dataInicial,
                                                  Date dataFinal, String statusOs, String statusItem) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT " +
                "  cod_os                                                                    AS OS,\n" +
                "  to_char(data_hora, 'DD/MM/YYYY HH24:MI')                                  AS \"ABERTURA OS\", " +
                "  to_char(data_hora + (prazo || ' hour') :: INTERVAL, 'DD/MM/YYYY HH24:MI') AS \"DATA LIMITE CONSERTO\", " +
                "  CASE WHEN status_os = 'A'\n" +
                "    THEN 'ABERTA'\n" +
                "  ELSE 'FECHADA' END                                                        AS \"STATUS OS\", " +
                "  placa_veiculo                                                             AS \"PLACA\", " +
                "  pergunta                                                                  AS \"PERGUNTA\", " +
                "  alternativa                                                               AS \"ALTERNATIVA\", " +
                "  prioridade                                                                AS \"PRIORIDADE\", " +
                "  prazo                                                                     AS \"PRAZO EM HORAS\", " +
                "  resposta                                                                  AS \"DESCRIÇÃO\", " +
                "  CASE WHEN status_ITEM = 'P'\n" +
                "    THEN 'PENDENTE'\n" +
                "  ELSE 'RESOLVIDO' END                                                      AS \"STATUS ITEM\", " +
                "  to_char(data_hora_conserto, 'DD/MM/YYYY HH24:MI')                           AS \"DATA CONSERTO\", " +
                "  nome_mecanico                                                            AS \"MECÂNICO\",\n" +
                "  feedback_conserto                                                         AS \"DESCRIÇÃO CONSERTO\", " +
                // PASSAR PRA MINUTOS
                "  tempo_realizacao / 60                                                     AS \"TEMPO DE CONSERTO\", " +
                "  km                                                                        AS \"KM ABERTURA\", " +
                "  km_fechamento                                                             AS \"KM FECHAMENTO\", " +
                "  coalesce((km_fechamento - km) :: TEXT, '-')                               AS \"KM PERCORRIDO\" " +
                "FROM estratificacao_os " +
                "WHERE cod_unidade = ? AND placa_veiculo LIKE ? AND (data_hora::DATE BETWEEN (? AT TIME ZONE ?) AND (? AT TIME ZONE ?)) AND " +
                "      status_os LIKE ? AND " +
                "      status_item LIKE ? " +
                "ORDER BY OS, \"PRAZO EM HORAS\";");
        final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId();
        stmt.setLong(1, codUnidade);
        stmt.setString(2, placa);
        stmt.setDate(3, dataInicial);
        stmt.setString(4, zoneId);
        stmt.setDate(5, dataFinal);
        stmt.setString(6, zoneId);
        stmt.setString(7, statusOs);
        stmt.setString(8, statusItem);
        return stmt;
    }

    @Override
    public void getEstratificacaoOsCsv(@NotNull OutputStream outputStream,
                                       @NotNull Long codUnidade,
                                       @NotNull String placa,
                                       @NotNull Date dataInicial,
                                       @NotNull Date dataFinal,
                                       @NotNull String statusOs,
                                       @NotNull String statusItem) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoOs(conn, codUnidade, placa, dataInicial, dataFinal, statusOs, statusItem);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getEstratificacaoOsReport(@NotNull Long codUnidade,
                                            @NotNull String placa,
                                            @NotNull Date dataInicial,
                                            @NotNull Date dataFinal,
                                            @NotNull String statusOs,
                                            @NotNull String statusItem) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoOs(conn, codUnidade, placa, dataInicial, dataFinal, statusOs, statusItem);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }
}