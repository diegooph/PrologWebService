package br.com.zalf.prolog.webservice.frota.checklist.relatorios;

import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.checklist.model.ChecksRealizadosAbaixoTempoEspecifico;
import br.com.zalf.prolog.webservice.frota.checklist.model.PlacasBloqueadas;
import br.com.zalf.prolog.webservice.frota.checklist.model.PlacasBloqueadasResponse;
import br.com.zalf.prolog.webservice.frota.checklist.model.QuantidadeChecklists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static br.com.zalf.prolog.webservice.commons.util.StatementUtils.bindValueOrNull;

/**
 * Created by luiz on 25/04/17.
 */
public class ChecklistRelatorioDaoImpl extends DatabaseConnection implements ChecklistRelatorioDao {

    public ChecklistRelatorioDaoImpl() {

    }

    @NotNull
    @Override
    public List<ChecksRealizadosAbaixoTempoEspecifico> getQtdChecksRealizadosAbaixoTempoEspecifico(
            @NotNull List<Long> codUnidades,
            final long tempoRealizacaoFiltragemMilis,
            final int diasRetroativosParaBuscar) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM " +
                    "FUNC_CHECKLIST_RELATORIO_REALIZADOS_ABAIXO_TEMPO_DEFINIDO(?, ?, ?, ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            stmt.setLong(2, tempoRealizacaoFiltragemMilis);
            stmt.setObject(3, Now.localDateUtc());
            stmt.setInt(4, diasRetroativosParaBuscar);

            rSet = stmt.executeQuery();
            final List<ChecksRealizadosAbaixoTempoEspecifico> checksRealizadosAbaixoTempoEspecifico = new ArrayList<>();
            while (rSet.next()) {
                checksRealizadosAbaixoTempoEspecifico.add(
                        new ChecksRealizadosAbaixoTempoEspecifico(
                                rSet.getString("UNIDADE"),
                                rSet.getString("NOME"),
                                rSet.getInt("QUANTIDADE CHECKLISTS REALIZADOS ABAIXO TEMPO ESPECIFICO"),
                                rSet.getInt("QUANTIDADE CHECKLISTS REALIZADOS")));
            }
            return checksRealizadosAbaixoTempoEspecifico;
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<QuantidadeChecklists> getQtdChecklistsRealizadosByTipo(@NotNull final List<Long> codUnidades,
                                                                       final int diasRetroativosParaBuscar)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM " +
                    "FUNC_CHECKLIST_RELATORIO_QTD_POR_TIPO(?, ?, ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            stmt.setObject(2, Now.localDateUtc());
            stmt.setInt(3, diasRetroativosParaBuscar);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<QuantidadeChecklists> checklists = new ArrayList<>();
                do {
                    checklists.add(new QuantidadeChecklists(
                            rSet.getObject("DATA", LocalDate.class),
                            rSet.getString("DATA_FORMATADA"),
                            rSet.getInt("TOTAL_CHECKLISTS_SAIDA"),
                            rSet.getInt("TOTAL_CHECKLISTS_RETORNO")));
                } while (rSet.next());
                return checklists;
            } else {
                throw new IllegalStateException("Erro ao buscar as informações de checklists realizados para as " +
                        "unidades: " + codUnidades);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getChecklistsRealizadosDiaAmbevCsv(@NotNull final OutputStream outputStream,
                                                   @NotNull final List<Long> codUnidades,
                                                   @NotNull final LocalDate dataInicial,
                                                   @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getChecklistRealizadosDiaAmbev(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getChecklistsRealizadosDiaAmbevReport(@NotNull final List<Long> codUnidades,
                                                        @NotNull final LocalDate dataInicial,
                                                        @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getChecklistRealizadosDiaAmbev(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getExtratoChecklistsRealizadosDiaAmbevCsv(@NotNull final OutputStream outputStream,
                                                          @NotNull final List<Long> codUnidades,
                                                          @NotNull final LocalDate dataInicial,
                                                          @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getExtratoChecklistsRealizadosDiaAmbev(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getExtratoChecklistsRealizadosDiaAmbevReport(@NotNull final List<Long> codUnidades,
                                                               @NotNull final LocalDate dataInicial,
                                                               @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getExtratoChecklistsRealizadosDiaAmbev(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getTempoRealizacaoChecklistsMotoristasCsv(@NotNull final OutputStream outputStream,
                                                          @NotNull final List<Long> codUnidades,
                                                          @NotNull final LocalDate dataInicial,
                                                          @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getTempoRealizacaoChecklistsMotorista(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getTempoRealizacaoChecklistsMotoristasReport(@NotNull final List<Long> codUnidades,
                                                               @NotNull final LocalDate dataInicial,
                                                               @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getTempoRealizacaoChecklistsMotorista(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    public Report getResumoChecklistsReport(@NotNull final List<Long> codUnidades,
                                            @NotNull final String placa,
                                            @NotNull final LocalDate dataInicial,
                                            @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getResumoChecklistsStatement(conn, codUnidades, placa, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    public void getResumoChecklistsCsv(@NotNull final OutputStream outputStream,
                                       @NotNull final List<Long> codUnidades,
                                       @NotNull final String placa,
                                       @NotNull final LocalDate dataInicial,
                                       @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getResumoChecklistsStatement(conn, codUnidades, placa, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }


    @Override
    public void getEstratificacaoRespostasNokCsv(@NotNull final OutputStream outputStream,
                                                 @NotNull final List<Long> codUnidades,
                                                 @NotNull final String placa,
                                                 @NotNull final LocalDate dataInicial,
                                                 @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoRespostasNokStatement(conn, codUnidades, placa, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getEstratificacaoRespostasNokReport(@NotNull final List<Long> codUnidades,
                                                      @NotNull final String placa,
                                                      @NotNull final LocalDate dataInicial,
                                                      @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoRespostasNokStatement(conn, codUnidades, placa, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getListagemModelosChecklistCsv(@NotNull final OutputStream outputStream,
                                               @NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getListagemModelosChecklistStatement(conn, codUnidades);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getListagemModelosChecklistReport(@NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getListagemModelosChecklistStatement(conn, codUnidades);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getDadosGeraisChecklistCsv(@NotNull final OutputStream outputStream,
                                           @NotNull final List<Long> codUnidades,
                                           @Nullable final Long codColaborador,
                                           @Nullable final String placa,
                                           @NotNull final LocalDate dataInicial,
                                           @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getDadosGeraisChecklistStatement(conn, codUnidades, codColaborador, placa, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
        } finally {
            closeConnection(conn, stmt, rSet);
        }

    }

    @NotNull
    @Override
    public Report getDadosGeraisChecklistReport(@NotNull final List<Long> codUnidades,
                                                @Nullable final Long codColaborador,
                                                @Nullable final String placa,
                                                @NotNull final LocalDate dataInicial,
                                                @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getDadosGeraisChecklistStatement(conn, codUnidades, codColaborador, placa, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public @NotNull Report getUltimoChecklistRealizadoPlacaReport(@NotNull final List<Long> codUnidades,
                                                                  @NotNull final List<Long> codTiposVeiculos)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getUltimoChecklistRealizadoPlacaStatement(conn, codUnidades, codTiposVeiculos);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getUltimoChecklistRealizadoPlacaCsv(@NotNull final OutputStream outputStream,
                                                    @NotNull final List<Long> codUnidades,
                                                    @NotNull final List<Long> codTiposVeiculos) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getUltimoChecklistRealizadoPlacaStatement(conn, codUnidades, codTiposVeiculos);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, outputStream);
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
            stmt = conn.prepareStatement("select * from  func_checklist_get_placas_bloqueadas(" +
                    "f_cod_unidades => ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));

            rSet = stmt.executeQuery();
            int qtdPlacasBloqueadas = 0;
            final List<PlacasBloqueadas> placasBloqueadas = new ArrayList<>();
            if (rSet.next()) {
                qtdPlacasBloqueadas = rSet.getInt("qtd_placas_bloqueadas");
                do {
                    placasBloqueadas.add(new PlacasBloqueadas(
                            rSet.getString("nome_unidade"),
                            rSet.getString("placa_bloqueada"),
                            rSet.getString("data_hora_abertura_os"),
                            rSet.getInt("qtd_itens_criticos")));
                } while (rSet.next());
                return new PlacasBloqueadasResponse(qtdPlacasBloqueadas, placasBloqueadas);
            } else {
                return new PlacasBloqueadasResponse(qtdPlacasBloqueadas, placasBloqueadas);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private PreparedStatement getDadosGeraisChecklistStatement(@NotNull final Connection conn,
                                                               @NotNull final List<Long> codUnidades,
                                                               @Nullable final Long codColaborador,
                                                               @Nullable final String placa,
                                                               @NotNull final LocalDate dataInicial,
                                                               @NotNull final LocalDate dataFinal)
            throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_RELATORIO_DADOS_GERAIS(?,?,?,?,?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        bindValueOrNull(stmt, 4, placa, SqlType.TEXT);
        bindValueOrNull(stmt, 5, codColaborador, SqlType.BIGINT);
        return stmt;
    }

    @NotNull
    private PreparedStatement getListagemModelosChecklistStatement(@NotNull final Connection conn,
                                                                   @NotNull final List<Long> codUnidades)
            throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_RELATORIO_LISTAGEM_MODELOS_CHECKLIST(?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        return stmt;
    }

    @NotNull
    private PreparedStatement getExtratoChecklistsRealizadosDiaAmbev(@NotNull final Connection conn,
                                                                     @NotNull final List<Long> codUnidades,
                                                                     @NotNull final LocalDate dataInicial,
                                                                     @NotNull final LocalDate dataFinal)
            throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_RELATORIO_AMBEV_EXTRATO_REALIZADOS_DIA(?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getChecklistRealizadosDiaAmbev(@NotNull final Connection conn,
                                                             @NotNull final List<Long> codUnidades,
                                                             @NotNull final LocalDate dataInicial,
                                                             @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_RELATORIO_AMBEV_REALIZADOS_DIA(?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getTempoRealizacaoChecklistsMotorista(@NotNull final Connection conn,
                                                                    @NotNull final List<Long> codUnidades,
                                                                    @NotNull final LocalDate dataInicial,
                                                                    @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_RELATORIO_TEMPO_REALIZACAO_MOTORISTAS(?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getResumoChecklistsStatement(@NotNull final Connection conn,
                                                           @NotNull final List<Long> codUnidades,
                                                           @NotNull final String placa,
                                                           @NotNull final LocalDate dataInicial,
                                                           @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_RELATORIO_RESUMO_REALIZADOS(?, ?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setString(2, placa);
        stmt.setObject(3, dataInicial);
        stmt.setObject(4, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getEstratificacaoRespostasNokStatement(@NotNull final Connection conn,
                                                                     @NotNull final List<Long> codUnidades,
                                                                     @NotNull final String placa,
                                                                     @NotNull final LocalDate dataInicial,
                                                                     @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_RELATORIO_ESTRATIFICACAO_RESPOSTAS_NOK(?, ?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setString(2, placa);
        stmt.setObject(3, dataInicial);
        stmt.setObject(4, dataFinal);
        return stmt;
    }

    @NotNull
    private PreparedStatement getUltimoChecklistRealizadoPlacaStatement(@NotNull final Connection conn,
                                                                        @NotNull final List<Long> codUnidades,
                                                                        @NotNull final List<Long> codTiposVeiculos)
            throws SQLException {
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " +
                "FUNC_CHECKLIST_RELATORIO_ULTIMO_CHECKLIST_REALIZADO_PLACA(" +
                "F_COD_UNIDADES => ?, " +
                "F_COD_TIPOS_VEICULOS => ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.BIGINT, codTiposVeiculos));
        return stmt;
    }
}