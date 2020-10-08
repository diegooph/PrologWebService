package br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios._model.AfericaoExportacaoProtheus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios.AfericaoRelatorioConverter.createAfericaoExportacaoProtheus;

/**
 * Created on 30/08/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class AfericaoRelatorioDaoImpl extends DatabaseConnection implements AfericaoRelatorioDao {

    @Override
    public void getCronogramaAfericoesPlacasCsv(@NotNull final OutputStream out,
                                                @NotNull final List<Long> codUnidades,
                                                @NotNull final String userToken) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getCronogramaAfericoesPlacasStmt(conn, codUnidades, userToken);
            rSet = stmt.executeQuery();
            new CsvWriter
                    .Builder(out)
                    .withResultSet(rSet)
                    .build()
                    .write();
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getCronogramaAfericoesPlacasReport(@NotNull final List<Long> codUnidades,
                                                     @NotNull final String userToken) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getCronogramaAfericoesPlacasStmt(conn, codUnidades, userToken);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public void getDadosGeraisAfericoesCsv(@NotNull final OutputStream out,
                                           @NotNull final List<Long> codUnidades,
                                           @NotNull final LocalDate dataInicial,
                                           @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getDadosGeraisAfericoesStmt(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter
                    .Builder(out)
                    .withResultSet(rSet)
                    .build()
                    .write();
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getDadosGeraisAfericoesReport(@NotNull final List<Long> codUnidades,
                                                @NotNull final LocalDate dataInicial,
                                                @NotNull final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getDadosGeraisAfericoesStmt(conn, codUnidades, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    @NotNull
    public List<AfericaoExportacaoProtheus> getExportacaoAfericoesProtheus(
            @NotNull final List<Long> codUnidades,
            @NotNull final List<Long> codVeiculos,
            @Nullable final LocalDate dataInicial,
            @Nullable final LocalDate dataFinal) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("select * from func_afericao_exportacao_afericoes_protheus(" +
                    "f_cod_unidades => ?," +
                    "f_cod_veiculos => ?," +
                    "f_data_inicial => ?," +
                    "f_data_final => ?);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
            stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.BIGINT, codVeiculos));
            if (dataInicial == null || dataFinal == null) {
                stmt.setNull(3, Types.DATE);
                stmt.setNull(4, Types.DATE);
            } else {
                stmt.setDate(3, DateUtils.toSqlDate(dataInicial));
                stmt.setDate(4, DateUtils.toSqlDate(dataFinal));
            }
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<AfericaoExportacaoProtheus> afericoesExportacaoProtheus = new ArrayList<>();
                do {
                    afericoesExportacaoProtheus.add(createAfericaoExportacaoProtheus(rSet));
                } while (rSet.next());
                return afericoesExportacaoProtheus;
            } else {
                return Collections.emptyList();
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private PreparedStatement getCronogramaAfericoesPlacasStmt(@NotNull final Connection conn,
                                                               @NotNull final List<Long> codUnidades,
                                                               @NotNull final String userToken) throws Throwable {
        final ZoneId zoneId = TimeZoneManager.getZoneIdForToken(userToken, conn);
        final LocalDateTime dataHoraGeracaoRelatorio = Now.localDateTimeUtc()
                .atZone(ZoneOffset.UTC)
                .withZoneSameInstant(zoneId)
                .toLocalDateTime();
        final PreparedStatement stmt =
                conn.prepareStatement("SELECT * FROM FUNC_AFERICAO_RELATORIO_CRONOGRAMA_AFERICOES_PLACAS(?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, OffsetDateTime.now(Clock.systemUTC()));
        stmt.setObject(3, dataHoraGeracaoRelatorio);
        return stmt;
    }

    @NotNull
    private PreparedStatement getDadosGeraisAfericoesStmt(@NotNull final Connection conn,
                                                          @NotNull final List<Long> codUnidades,
                                                          @NotNull final LocalDate dataInicial,
                                                          @NotNull final LocalDate dataFinal) throws Throwable {
        final PreparedStatement stmt =
                conn.prepareStatement("SELECT * FROM FUNC_AFERICAO_RELATORIO_DADOS_GERAIS(?, ?, ?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        stmt.setObject(2, dataInicial);
        stmt.setObject(3, dataFinal);
        return stmt;
    }
}