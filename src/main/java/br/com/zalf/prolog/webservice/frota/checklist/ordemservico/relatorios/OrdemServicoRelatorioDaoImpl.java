package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.relatorios;

import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.PlacasBloqueadas;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.PlacasBloqueadasResponse;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.OLD.ItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.PlacaItensOsAbertos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.*;

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
            close(conn, stmt, rSet);
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
            close(conn, stmt, rSet);
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
            close(conn, stmt, rSet);
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
            close(conn, stmt, rSet);
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
            close(conn, stmt, rSet);
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
            close(conn, stmt, rSet);
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
            close(conn, stmt, rSet);
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
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getEstratificacaoOsCsv(@NotNull final OutputStream outputStream,
                                       @NotNull final List<Long> codUnidades,
                                       @NotNull final String placa,
                                       @NotNull final String statusOs,
                                       @NotNull final String statusItemOs,
                                       @Nullable final LocalDate dataInicialAbertura,
                                       @Nullable final LocalDate dataFinalAbertura,
                                       @Nullable final LocalDate dataInicialResolucao,
                                       @Nullable final LocalDate dataFinalResolucao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoOs(conn, codUnidades, placa, statusOs, statusItemOs,
                    dataInicialAbertura,
                    dataFinalAbertura,
                    dataInicialResolucao,
                    dataFinalResolucao);
            rSet = stmt.executeQuery();
            new CsvWriter
                    .Builder(outputStream)
                    .withResultSet(rSet)
                    .build()
                    .write();
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getEstratificacaoOsReport(@NotNull final List<Long> codUnidades,
                                            @NotNull final String placa,
                                            @NotNull final String statusOs,
                                            @NotNull final String statusItemOs,
                                            @Nullable final LocalDate dataInicialAbertura,
                                            @Nullable final LocalDate dataFinalAbertura,
                                            @Nullable final LocalDate dataInicialResolucao,
                                            @Nullable final LocalDate dataFinalResolucao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoOs(conn, codUnidades, placa, statusOs, statusItemOs,
                    dataInicialAbertura,
                    dataFinalAbertura,
                    dataInicialResolucao,
                    dataFinalResolucao);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    @NotNull
    public PlacasBloqueadasResponse getPlacasBloqueadas(@NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from  func_checklist_os_relatorio_get_placas_bloqueadas(" +
                    "f_cod_unidades => ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));

            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<PlacasBloqueadas> placasBloqueadas = new ArrayList<>();
                do {
                    placasBloqueadas.add(new PlacasBloqueadas(
                            rSet.getString("nome_unidade"),
                            rSet.getString("placa_bloqueada"),
                            rSet.getString("data_hora_abertura_os"),
                            rSet.getInt("qtd_itens_criticos")));
                } while (rSet.next());
                return new PlacasBloqueadasResponse(placasBloqueadas.size(), placasBloqueadas);
            } else {
                return new PlacasBloqueadasResponse(0, Collections.emptyList());
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private PreparedStatement getEstratificacaoOs(@NotNull final Connection conn,
                                                  @NotNull final List<Long> codUnidades,
                                                  @NotNull final String placa,
                                                  @NotNull final String statusOs,
                                                  @NotNull final String statusItemOs,
                                                  @Nullable final LocalDate dataInicialAbertura,
                                                  @Nullable final LocalDate dataFinalAbertura,
                                                  @Nullable final LocalDate dataInicialResolucao,
                                                  @Nullable final LocalDate dataFinalResolucao) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_OS_RELATORIO_ESTRATIFICACAO_OS(?, ?, ?, ?, ?, ?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setString(2, placa);
        stmt.setString(3, statusOs);
        stmt.setString(4, statusItemOs);
        stmt.setObject(5, dataInicialAbertura);
        stmt.setObject(6, dataFinalAbertura);
        stmt.setObject(7, dataInicialResolucao);
        stmt.setObject(8, dataFinalResolucao);
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