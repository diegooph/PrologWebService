package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.relatorios;

import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.ItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.OrdemServico;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by luiz on 26/04/17.
 */
public class OrdemServicoRelatorioDaoImpl extends DatabaseConnection implements OrdemServicoRelatorioDao {

    @Override
    public void getItensMaiorQuantidadeNokCsv(@NotNull final OutputStream outputStream,
                                              @NotNull final List<Long> codUnidades,
                                              @NotNull final LocalDate dataInicial,
                                              @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getItensMaiorQuantidadeNok(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getItensMaiorQuantidadeNokReport(@NotNull final List<Long> codUnidades,
                                                   @NotNull final LocalDate dataInicial,
                                                   @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getItensMaiorQuantidadeNok(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getMediaTempoConsertoItemCsv(@NotNull final OutputStream outputStream,
                                             @NotNull final List<Long> codUnidades,
                                             @NotNull final LocalDate dataInicial,
                                             @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getMediaTempoConsertoItem(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @org.jetbrains.annotations.NotNull
    @Override
    public Report getMediaTempoConsertoItemReport(@NotNull final List<Long> codUnidades,
                                                  @NotNull final LocalDate dataInicial,
                                                  @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getMediaTempoConsertoItem(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getProdutividadeMecanicosCsv(@NotNull final OutputStream outputStream,
                                             @NotNull final List<Long> codUnidades,
                                             @NotNull final LocalDate dataInicial,
                                             @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getProdutividadeMecanicos(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @org.jetbrains.annotations.NotNull
    @Override
    public Report getProdutividadeMecanicosReport(@NotNull final List<Long> codUnidades,
                                                  @NotNull final LocalDate dataInicial,
                                                  @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getProdutividadeMecanicos(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getEstratificacaoOsCsv(@NotNull final OutputStream outputStream,
                                       @NotNull final List<Long> codUnidades,
                                       @NotNull final String placa,
                                       @NotNull final OrdemServico.Status statusOs,
                                       @NotNull final ItemOrdemServico.Status statusItemOs,
                                       @NotNull final LocalDate dataInicial,
                                       @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoOs(conn, codUnidades, placa, statusOs, statusItemOs, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getEstratificacaoOsReport(@NotNull final List<Long> codUnidades,
                                            @NotNull final String placa,
                                            @NotNull final OrdemServico.Status statusOs,
                                            @NotNull final ItemOrdemServico.Status statusItemOs,
                                            @NotNull final LocalDate dataInicial,
                                            @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoOs(conn, codUnidades, placa, statusOs, statusItemOs, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    private PreparedStatement getEstratificacaoOs(@NotNull final Connection conn,
                                                  @NotNull final List<Long> codUnidades,
                                                  @NotNull final String placa,
                                                  @NotNull final OrdemServico.Status statusOs,
                                                  @NotNull final ItemOrdemServico.Status statusItemOs,
                                                  @NotNull final LocalDate dataInicial,
                                                  @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("select * from func_relatorio_estratificacao_os " +
                "(?,?,?,?,?,?,?);");
//        final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidades, conn).getId();
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setString(2, placa);
        stmt.setObject(3, dataInicial);
        stmt.setObject(4, dataFinal);
//        stmt.setString(5, zoneId);
//        stmt.setString(6, statusOs);
//        stmt.setString(7, statusItem);
        return stmt;
    }

    @NotNull
    private PreparedStatement getProdutividadeMecanicos(@NotNull final Connection conn,
                                                        @NotNull final List<Long> codUnidades,
                                                        @NotNull final LocalDate dataInicial,
                                                        @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT " +
                "nome_mecanico AS \"MECÂNICO\", " +
                "count(nome_mecanico) as \"CONSERTOS\", " +
                "sum(tempo_realizacao/3600000) as \"HORAS\", " +
                "round(avg(tempo_realizacao/3600000)) as \"HORAS POR CONSERTO\" " +
                "FROM estratificacao_os " +
                "WHERE tempo_realizacao is not null and tempo_realizacao > 0 and " +
                "cod_unidade = ? and data_hora BETWEEN (? AT TIME ZONE ?) AND (? AT TIME ZONE ?) " +
                "GROUP BY 1 " +
                "ORDER BY nome_mecanico;");
//        final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidades, conn).getId();
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
//        stmt.setString(3, zoneId);
        stmt.setObject(4, dataFinal);
//        stmt.setString(5, zoneId);
        return stmt;
    }

    @NotNull
    private PreparedStatement getMediaTempoConsertoItem(@NotNull final Connection conn,
                                                        @NotNull final List<Long> codUnidades,
                                                        @NotNull final LocalDate dataInicial,
                                                        @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT " +
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
//        final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidades, conn).getId();
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
//        stmt.setString(3, zoneId);
        stmt.setObject(4, dataFinal);
//        stmt.setString(5, zoneId);
        return stmt;
    }

    @NotNull
    private PreparedStatement getItensMaiorQuantidadeNok(@NotNull final Connection conn,
                                                         @NotNull final List<Long> codUnidades,
                                                         @NotNull final LocalDate dataInicial,
                                                         @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT " +
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
//        final String zoneId = TimeZoneManager.getZoneIdForCodUnidade(codUnidades, conn).getId();
        // TODO
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
//        stmt.setString(3, zoneId);
        stmt.setObject(4, dataFinal);
//        stmt.setString(5, zoneId);
        return stmt;
    }
}