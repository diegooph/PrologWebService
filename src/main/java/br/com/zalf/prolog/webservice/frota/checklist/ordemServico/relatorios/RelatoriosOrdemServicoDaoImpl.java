package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.relatorios;

import br.com.zalf.prolog.commons.Report;
import br.com.zalf.prolog.webservice.CsvWriter;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.report.ReportConverter;

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
            return ReportConverter.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getMediaTempoConsertoItemCsv(@NotNull OutputStream outputStream, @NotNull Long codUnidade, @NotNull Date dataInicial, @NotNull Date dataFinal) throws SQLException, IOException {
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
    public Report getMediaTempoConsertoItemReport(@NotNull Long codUnidade, @NotNull Date dataInicial, @NotNull Date dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getMediaTempoConsertoItem(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportConverter.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getProdutividadeMecanicosCsv(@NotNull OutputStream outputStream, @NotNull Long codUnidade, @NotNull Date dataInicial, @NotNull Date dataFinal) throws SQLException, IOException {
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
    public Report getProdutividadeMecanicosReport(@NotNull Long codUnidade, @NotNull Date dataInicial, @NotNull Date dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getProdutividadeMecanicos(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportConverter.createReport(rSet);
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
        PreparedStatement stmt = conn.prepareStatement("SELECT pergunta, alternativa, prioridade, prazo_conserto_em_horas,     \n" +
                "qt_apontados, qt_resolvidos_dentro_prazo,\n" +
                "trunc(md_tempo_conserto_segundos/3600) || ' / ' ||  \n" +
                "trunc(md_tempo_conserto_segundos/60) as \n" +
                "md_tempo_conserto_em_horas_em_minutos,\n" +
                "round((qt_resolvidos_dentro_prazo/qt_apontados::float)*100) || '%' as \n" +
                "porcentagem\n" +
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
        PreparedStatement stmt = conn.prepareStatement("SELECT nome_mecanico, count(nome_mecanico) as itens_consertados \n" +
                ",sum(tempo_realizacao/3600000) as horas_consertando, -- millis to \n" +
                "minutes\n" +
                "round(avg(tempo_realizacao/3600000)) as md_horas_conserto_item\n" +
                "FROM estratificacao_os\n" +
                "WHERE tempo_realizacao is not null and tempo_realizacao > 0 and \n" +
                "cod_unidade = ? and data_hora BETWEEN ? AND ?\n" +
                "GROUP BY 1\n" +
                "ORDER BY nome_mecanico");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, dataInicial);
        stmt.setDate(3, dataFinal);
        return stmt;
    }
}