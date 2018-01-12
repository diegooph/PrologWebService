package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.relatorios;

import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.CsvWriter;
import br.com.zalf.prolog.webservice.DatabaseConnection;
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
        PreparedStatement stmt = conn.prepareStatement("SELECT cp.pergunta, cap.alternativa, prioridade,\n" +
                "sum( case when cr.resposta <> 'OK' then 1 else 0 end ) as contagem,\n" +
                "count(cp.pergunta) as total,\n" +
                "trunc((sum( case when cr.resposta <> 'OK' then 1 else 0 end ) / count(cp.pergunta)::float) * 100) || '%' as proporcao\n" +
                "FROM checklist c\n" +
                "JOIN checklist_respostas cr ON c.cod_unidade = cr.cod_unidade AND cr.cod_checklist_modelo = c.cod_checklist_modelo\n" +
                "JOIN checklist_perguntas cp ON cp.cod_unidade = C.cod_unidade AND cp.codigo = CR.cod_pergunta AND cp.cod_checklist_modelo = cr.cod_checklist_modelo\n" +
                "JOIN veiculo v ON v.placa::text = c.placa_veiculo::text\n" +
                "JOIN checklist_alternativa_pergunta cap ON cap.cod_unidade = cp.cod_unidade AND cap.cod_checklist_modelo = cp.cod_checklist_modelo\n" +
                "AND cap.cod_pergunta = cp.codigo AND cap.codigo = cr.cod_alternativa\n" +
                "AND cr.cod_checklist = c.codigo AND cr.cod_pergunta = cp.codigo AND cr.cod_alternativa = cap.codigo\n" +
                "WHERE c.cod_unidade = ? and c.data_hora BETWEEN ? and ?\n" +
                "GROUP BY 1, 2, 3\n" +
                "ORDER BY trunc((sum( case when cr.resposta <> 'OK' then 1 else 0 end ) / count(cp.pergunta)::float) * 100) desc");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, dataInicial);
        stmt.setDate(3, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getMediaTempoConsertoItem(Connection conn, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT pergunta, alternativa, prioridade, prazo_conserto_em_horas AS \"PRAZO CONSERTO EM HORAS\",     \n" +
                "qt_apontados AS \"QT APONTADOS\", qt_resolvidos_dentro_prazo AS \"QT RESOLVIDOS DENTRO PRAZO\",\n" +
                "trunc(md_tempo_conserto_segundos/3600) || ' / ' ||  \n" +
                "trunc(md_tempo_conserto_segundos/60) as \"MD TEMPO CONSERTO HORAS/MINUTOS\",\n" +
                "round((qt_resolvidos_dentro_prazo/qt_apontados::float)*100) || '%' as \n" +
                " \"PORCENTAGEM\"\n" +
                "FROM\n" +
                "(SELECT pergunta,\n" +
                "alternativa,\n" +
                "prioridade,\n" +
                "prazo as prazo_conserto_em_horas,\n" +
                "count(pergunta) as qt_apontados,\n" +
                "sum(case when (extract(epoch from (data_hora_conserto - \n" +
                "data_hora))/3600) <= prazo then 1 else 0 end) as \n" +
                "qt_resolvidos_dentro_prazo,\n" +
                "trunc(extract( epoch from avg(data_hora_conserto - \n" +
                "estratificacao_os.data_hora))) as md_tempo_conserto_segundos\n" +
                "FROM estratificacao_os\n" +
                "WHERE cod_unidade = ? AND data_hora BETWEEN ? AND ?\n" +
                "GROUP BY 1, 2, 3, 4) as dados\n" +
                "ORDER BY round((qt_resolvidos_dentro_prazo/qt_apontados::float)*100) \n" +
                "desc;");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, dataInicial);
        stmt.setDate(3, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getProdutividadeMecanicos(Connection conn, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT nome_mecanico AS \"MECÂNICO\", count(nome_mecanico) as \"CONSERTOS\",\n" +
                "  sum(tempo_realizacao/3600000) as \"HORAS\",\n" +
                "  round(avg(tempo_realizacao/3600000)) as \"HORAS POR CONSERTO\"\n" +
                "  FROM estratificacao_os\n" +
                "  WHERE tempo_realizacao is not null and tempo_realizacao > 0 and\n" +
                "  cod_unidade = ? and data_hora BETWEEN ? AND ?\n" +
                "  GROUP BY 1\n" +
                "  ORDER BY nome_mecanico");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, dataInicial);
        stmt.setDate(3, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getEstratificacaoOs(Connection conn, Long codUnidade, String placa, Date dataInicial,
                                                  Date dataFinal, String statusOs, String statusItem) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT\n" +
                "  cod_os                                                                    AS OS,\n" +
                "  to_char(data_hora, 'DD/MM/YYYY HH24:MI')                                  AS \"ABERTURA OS\",\n" +
                "  to_char(data_hora + (prazo || ' hour') :: INTERVAL, 'DD/MM/YYYY HH24:MI') AS \"DATA LIMITE CONSERTO\",\n" +
                "  CASE WHEN status_os = 'A'\n" +
                "    THEN 'ABERTA'\n" +
                "  ELSE 'FECHADA' END                                                        AS \"STATUS OS\",\n" +
                "  placa_veiculo                                                             AS \"PLACA\",\n" +
                "  pergunta                                                                  AS \"PERGUNTA\",\n" +
                "  alternativa                                                               AS \"ALTERNATIVA\",\n" +
                "  prioridade                                                                AS \"PRIORIDADE\",\n" +
                "  prazo                                                                     AS \"PRAZO EM HORAS\",\n" +
                "  resposta                                                                  AS \"DESCRIÇÃO\",\n" +
                "  CASE WHEN status_ITEM = 'P'\n" +
                "    THEN 'PENDENTE'\n" +
                "  ELSE 'RESOLVIDO' END                                                      AS \"STATUS ITEM\",\n" +
                "  to_char(data_hora_conserto, 'DD/MM/YYYY HH24:MI')                           AS \"DATA CONSERTO\",\n" +
                "  nome_mecanico                                                            AS \"MECÂNICO\",\n" +
                "  feedback_conserto                                                         AS \"DESCRIÇÃO CONSERTO\",\n" +
                "  --   PASSAR PRA MINUTOS\n" +
                "  tempo_realizacao / 60                                                     AS \"TEMPO DE CONSERTO\",\n" +
//                "  CASE WHEN data_hora_conserto IS NULL\n" +
//                "    THEN '-'\n" +
//                "  ELSE\n" +
//                "    CASE WHEN data_hora_conserto <= data_hora + (prazo || ' hour') :: INTERVAL\n" +
//                "      THEN 'SIM'\n" +
//                "    ELSE 'NÃO' END\n" +
//                "  END                                                                       AS \"CUMPRIU PRAZO\",\n" +
                "  km                                                                        AS \"KM ABERTURA\",\n" +
                "  km_fechamento                                                             AS \"KM FECHAMENTO\",\n" +
                "  coalesce((km_fechamento - km) :: TEXT, '-')                               AS \"KM PERCORRIDO\"\n" +
                "FROM estratificacao_os\n" +
                "WHERE cod_unidade = ? AND placa_veiculo LIKE ? AND (data_hora :: DATE BETWEEN ? AND ?) AND\n" +
                "      status_os LIKE ? AND\n" +
                "      status_item LIKE ?\n" +
                "ORDER BY OS, \"PRAZO EM HORAS\";");
        stmt.setLong(1, codUnidade);
        stmt.setString(2, placa);
        stmt.setDate(3, dataInicial);
        stmt.setDate(4, dataFinal);
        stmt.setString(5, statusOs);
        stmt.setString(6, statusItem);
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