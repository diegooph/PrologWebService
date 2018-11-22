package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.relatorios;

import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.OLD.ItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.PlacaItensOsAbertos;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luiz on 26/04/17.
 */
public final class OrdemServicoRelatorioDaoImpl extends DatabaseConnection implements OrdemServicoRelatorioDao {

    @NotNull
    @Override
    public List<PlacaItensOsAbertos> getPlacasMaiorQtdItensOsAbertos(@NotNull final List<Long> codUnidades,
                                                                     final int qtdPlacasParaBuscar) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM " +
                    "FUNC_CHECKLIST_OS_RELATORIO_PLACAS_MAIOR_QTD_ITENS_ABERTOS(?, ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            stmt.setInt(2, qtdPlacasParaBuscar);
            rSet = stmt.executeQuery();
            final List<PlacaItensOsAbertos> itensOsAbertos = new ArrayList<>();
            while (rSet.next()) {
                itensOsAbertos.add(
                        new PlacaItensOsAbertos(
                                rSet.getString("NOME_UNIDADE"),
                                rSet.getString("PLACA"),
                                rSet.getInt("QUANTIDADE_ITENS_ABERTOS"),
                                rSet.getInt("QUANTIDADE_ITENS_CRITICOS_ABERTOS")));
            }
            return itensOsAbertos;
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Map<PrioridadeAlternativa, Integer> getQtdItensOsByPrioridade(
            @NotNull final List<Long> codUnidades,
            @NotNull final ItemOrdemServico.Status statusItensContagem) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM " +
                    "FUNC_CHECKLIST_OS_RELATORIO_QTD_ITENS_POR_PRIORIDADE(?, ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            stmt.setString(2, statusItensContagem.asString());
            rSet = stmt.executeQuery();
            final Map<PrioridadeAlternativa, Integer> qtdItensOs = new LinkedHashMap<>();
            while (rSet.next()) {
                qtdItensOs.put(
                        PrioridadeAlternativa.fromString(rSet.getString("PRIORIDADE")),
                        rSet.getInt("QUANTIDADE"));
            }
            return qtdItensOs;
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

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

    @NotNull
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

    @NotNull
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
                                       @NotNull final String statusOs,
                                       @NotNull final String statusItemOs,
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
                                            @NotNull final String statusOs,
                                            @NotNull final String statusItemOs,
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
                                                  @NotNull final String statusOs,
                                                  @NotNull final String statusItemOs,
                                                  @NotNull final LocalDate dataInicial,
                                                  @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_OS_RELATORIO_ESTRATIFICACAO_OS(?, ?, ?, ?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setString(2, placa);
        stmt.setString(3, statusOs);
        stmt.setString(4, statusItemOs);
        stmt.setObject(5, dataInicial);
        stmt.setObject(6, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getProdutividadeMecanicos(@NotNull final Connection conn,
                                                        @NotNull final List<Long> codUnidades,
                                                        @NotNull final LocalDate dataInicial,
                                                        @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_OS_RELATORIO_PRODUTIVIDADE_MECANICOS(?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getMediaTempoConsertoItem(@NotNull final Connection conn,
                                                        @NotNull final List<Long> codUnidades,
                                                        @NotNull final LocalDate dataInicial,
                                                        @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_OS_RELATORIO_MEDIA_TEMPO_CONSERTO_ITEM(?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getItensMaiorQuantidadeNok(@NotNull final Connection conn,
                                                         @NotNull final List<Long> codUnidades,
                                                         @NotNull final LocalDate dataInicial,
                                                         @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_OS_RELATORIO_ITENS_MAIOR_QUANTIDADE_NOK(?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }
}