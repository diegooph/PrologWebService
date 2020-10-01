package br.com.zalf.prolog.webservice.frota.veiculo.relatorio;

import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created on 1/25/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class VeiculoRelatorioDaoImpl extends DatabaseConnection implements VeiculoRelatorioDao {

    @Override
    public int getQtdVeiculosAtivos(@NotNull final List<Long> codUnidades) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT COUNT(DISTINCT V.PLACA) AS TOTAL_VEICULOS " +
                    "FROM VEICULO AS V " +
                    "WHERE V.COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND V.STATUS_ATIVO IS TRUE;");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.TEXT, codUnidades));
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getInt("TOTAL_VEICULOS");
            } else {
                return 0;
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getListagemVeiculosByUnidadeCsv(@NotNull final OutputStream out,
                                                @NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getListagemVeiculosByUnidadeStmt(conn, codUnidades);
            rSet = stmt.executeQuery();
            new CsvWriter
                    .Builder(out)
                    .withResultSet(rSet)
                    .build()
                    .write();
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getListagemVeiculosByUnidadeReport(@NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getListagemVeiculosByUnidadeStmt(conn, codUnidades);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public void getEvolucaoKmCsv(@NotNull final OutputStream out,
                                 @NotNull final Long codEmpresa,
                                 @NotNull final Long codVeiculo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEvolucaoKmStmt(conn, codEmpresa, codVeiculo);
            rSet = stmt.executeQuery();
            new CsvWriter
                    .Builder(out)
                    .withResultSet(rSet)
                    .build()
                    .write();
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Override
    public @NotNull Report getEvolucaoKmReport(@NotNull final Long codEmpresa,
                                               @NotNull final Long codVeiculo) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEvolucaoKmStmt(conn, codEmpresa, codVeiculo);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    private PreparedStatement getListagemVeiculosByUnidadeStmt(@NotNull final Connection conn,
                                                               @NotNull final List<Long> codUnidades) throws Throwable {
        final PreparedStatement stmt =
                conn.prepareStatement("SELECT * FROM FUNC_VEICULO_RELATORIO_LISTAGEM_VEICULOS_BY_UNIDADE(?);");
        stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.BIGINT, codUnidades));
        return stmt;
    }

    @NotNull
    private PreparedStatement getEvolucaoKmStmt(@NotNull final Connection conn,
                                                @NotNull final Long codEmpresa,
                                                @NotNull final Long codVeiculo) throws Throwable {
        final PreparedStatement stmt =
                conn.prepareStatement("select * from func_veiculo_relatorio_evolucao_km_consolidado(" +
                        "f_cod_empresa => ?, " +
                        "f_cod_veiculo => ?);");
        stmt.setLong(1, codEmpresa);
        stmt.setLong(2, codVeiculo);
        return stmt;
    }
}