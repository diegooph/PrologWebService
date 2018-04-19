package br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ControleIntervaloDao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.FolhaPontoDia;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.FolhaPontoRelatorio;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.Intervalo;
import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoIntervalo;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Date;

/**
 * Created by Zart on 28/08/2017.
 */
public class ControleIntervaloRelatorioDaoImpl extends DatabaseConnection implements ControleIntervaloRelatoriosDao {
    private static final String TAG = ControleIntervaloRelatorioDaoImpl.class.getSimpleName();

    @Override
    public void getIntervalosCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
            throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getIntervalosStmt(codUnidade, dataInicial, dataFinal, cpf, conn);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getIntervalosReport(Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
            throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getIntervalosStmt(codUnidade, dataInicial, dataFinal, cpf, conn);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private PreparedStatement getIntervalosStmt(Long codUnidade, Date dataInicial, Date dataFinal, String cpf, Connection conn)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM FUNC_RELATORIO_MARCACAO_PONTO_REALIZADOS" +
                "(?, ?, ?, ?);");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, DateUtils.toSqlDate(dataInicial));
        stmt.setDate(3, DateUtils.toSqlDate(dataFinal));
        stmt.setString(4, cpf);
        return stmt;
    }

    @Override
    public void getIntervalosMapasCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getIntervalosMapasStmt(codUnidade, dataInicial, dataFinal, conn);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getIntervalosMapasReport(Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getIntervalosMapasStmt(codUnidade, dataInicial, dataFinal, conn);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private PreparedStatement getIntervalosMapasStmt(Long codUnidade, Date dataInicial, Date dataFinal, Connection conn)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM func_relatorio_intervalos_mapas(?,?,?)");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, DateUtils.toSqlDate(dataInicial));
        stmt.setDate(3, DateUtils.toSqlDate(dataFinal));
        return stmt;
    }

    @Override
    public void getAderenciaIntervalosDiariaCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getAderenciaIntervalosDiariaStmt(codUnidade, dataInicial, dataFinal, conn);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getAderenciaIntervalosDiariaReport(Long codUnidade, Date dataInicial, Date dataFinal)
            throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getAderenciaIntervalosDiariaStmt(codUnidade, dataInicial, dataFinal, conn);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private PreparedStatement getAderenciaIntervalosDiariaStmt(Long codUnidade, Date dataInicial, Date dataFinal, Connection conn)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM func_relatorio_aderencia_intervalo_dias(?,?,?)");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, DateUtils.toSqlDate(dataInicial));
        stmt.setDate(3, DateUtils.toSqlDate(dataFinal));
        return stmt;
    }

    @Override
    public void getAderenciaIntervalosColaboradorCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
            throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getAderenciaIntervalosColaboradorStmt(codUnidade, dataInicial, dataFinal, conn, cpf);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public Report getAderenciaIntervalosColaboradorReport(Long codUnidade, Date dataInicial, Date dataFinal,  String cpf)
            throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getAderenciaIntervalosColaboradorStmt(codUnidade, dataInicial, dataFinal, conn, cpf);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    private PreparedStatement getAderenciaIntervalosColaboradorStmt(Long codUnidade, Date dataInicial, Date dataFinal, Connection conn,  String cpf)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM func_relatorio_aderencia_intervalo_colaborador(?,?,?,?)");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, DateUtils.toSqlDate(dataInicial));
        stmt.setDate(3, DateUtils.toSqlDate(dataFinal));
        stmt.setString(4, cpf);
        return stmt;
    }

    @Override
    public void getRelatorioPadraoPortaria1510Csv(@NotNull final OutputStream out,
                                                  @NotNull final Long codUnidade,
                                                  @NotNull final Long codTipoIntervalo,
                                                  @NotNull final String cpf,
                                                  @NotNull final LocalDate dataInicial,
                                                  @NotNull final LocalDate dataFinal) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getRelatorioPadraoPortaria1510Stmt(codUnidade, codTipoIntervalo, cpf, dataInicial, dataFinal, conn);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    private PreparedStatement getRelatorioPadraoPortaria1510Stmt(@NotNull final Long codUnidade,
                                                                 @NotNull final Long codTipoIntervalo,
                                                                 @NotNull final String cpf,
                                                                 @NotNull final LocalDate dataInicial,
                                                                 @NotNull final LocalDate dataFinal,
                                                                 @NotNull final Connection conn) throws SQLException {
        Preconditions.checkNotNull(codUnidade);
        final PreparedStatement stmt = conn.prepareStatement("SELECT * FROM func_relatorio_intervalo_portaria_1510_tipo_3(?, ?, ?, ?, ?, ?);");
        stmt.setLong(1, codUnidade);
        stmt.setLong(2, codTipoIntervalo);
        if (!cpf.equals("%")) {
            stmt.setLong(3, Long.parseLong(cpf));
        } else {
            stmt.setNull(3, Types.BIGINT);
        }
        stmt.setObject(4, dataInicial);
        stmt.setObject(5, dataFinal);
        stmt.setString(6, TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId());
        return stmt;
    }

    @NotNull
    @Override
    public List<FolhaPontoRelatorio> getFolhaPontoRelatorio(@NotNull final Long codUnidade,
                                                            @NotNull final String codTipoIntervalo,
                                                            @NotNull final String cpf,
                                                            @NotNull final LocalDate dataInicial,
                                                            @NotNull final LocalDate dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_RELATORIO_INTERVALO_FOLHA_DE_PONTO(?, ?, ?, ?, ?, ?);");
            stmt.setLong(1, codUnidade);
            if (codTipoIntervalo.equals("%")) {
                stmt.setNull(2, Types.BIGINT);
            } else {
                stmt.setLong(2, Long.parseLong(codTipoIntervalo));
            }
            if (cpf.equals("%")) {
                stmt.setNull(3, Types.BIGINT);
            } else {
                stmt.setLong(3, Long.parseLong(cpf));
            }
            stmt.setObject(4, dataInicial);
            stmt.setObject(5, dataFinal);
            stmt.setString(6, TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn).getId());
            rSet = stmt.executeQuery();

            final List<FolhaPontoRelatorio> relatorios = new ArrayList<>();
            final ControleIntervaloDao dao = Injection.provideControleIntervaloDao();
            final Map<Long, TipoIntervalo> tiposIntervalosUnidade = tiposIntervalosToMap(dao.getTiposIntervalosByUnidade(codUnidade, false));
            Set<TipoIntervalo> tiposIntervalosMarcados = new HashSet<>();
            Long cpfAnterior = null;
            String nomeAnterior = null;
            LocalDate diaAnterior = null;
            List<FolhaPontoDia> dias = new ArrayList<>();
            List<Intervalo> intervalosDia = new ArrayList<>();
            while (rSet.next()) {
                final Long cpfAtual = rSet.getLong("CPF_COLABORADOR");
                final LocalDate diaAtual = rSet.getObject("DATA_HORA_INICIO", LocalDateTime.class).toLocalDate();

                // Se for na primeira iteração, devemos deixar dia e cpf anterior como sendo igual aos atuais.
                if (cpfAnterior == null) {
                    cpfAnterior = cpfAtual;
                }
                if (diaAnterior == null) {
                    diaAnterior = diaAtual;
                }

                if (!cpfAnterior.equals(cpfAtual)) {
                    // Trocou de colaborador.
                    Log.d(TAG, "Colaborador alterado. Anterior: " + cpfAnterior + " - Atual: " + cpfAtual);
                    dias.add(new FolhaPontoDia(diaAnterior, intervalosDia));
                    final Colaborador colaborador = new Colaborador();
                    colaborador.setCpf(cpfAnterior);
                    colaborador.setNome(nomeAnterior);
                    relatorios.add(new FolhaPontoRelatorio(colaborador, tiposIntervalosMarcados, dias));
                    tiposIntervalosMarcados = new HashSet<>();
                    dias = new ArrayList<>();
                    intervalosDia = new ArrayList<>();
                } else {
                    // Mesmo colaborador.
                    Log.d(TAG, "Mesmo colaborador. Anterior: " + cpfAnterior + " - Atual: " + cpfAtual);

                    if (!diaAnterior.equals(diaAtual)) {
                        // Trocou o dia.
                        Log.d(TAG, "Mesmo dia. Anterior: " + diaAnterior + " - Atual: " + diaAtual);
                        dias.add(new FolhaPontoDia(diaAnterior, intervalosDia));
                        intervalosDia = new ArrayList<>();
                    } else {
                        // Mesmo dia.
                        Log.d(TAG, "Dia alterado. Anterior: " + diaAnterior + " - Atual: " + diaAtual);
                    }
                }

                final Intervalo intervalo = new Intervalo();
                intervalo.setDataHoraInicio(rSet.getObject("DATA_HORA_INICIO", LocalDateTime.class));
                intervalo.setDataHoraFim(rSet.getObject("DATA_HORA_FIM", LocalDateTime.class));
                intervalosDia.add(intervalo);
                tiposIntervalosMarcados.add(tiposIntervalosUnidade.get(rSet.getLong("COD_TIPO_INTERVALO")));

                cpfAnterior = cpfAtual;
                diaAnterior = diaAtual;
                nomeAnterior = rSet.getString("NOME_COLABORADOR");
            }
            if (diaAnterior != null) {
                dias.add(new FolhaPontoDia(diaAnterior, intervalosDia));
                final Colaborador colaborador = new Colaborador();
                colaborador.setCpf(cpfAnterior);
                colaborador.setNome(nomeAnterior);
                relatorios.add(new FolhaPontoRelatorio(colaborador, tiposIntervalosMarcados, dias));
            }
            return relatorios;
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    private Map<Long, TipoIntervalo> tiposIntervalosToMap(@NotNull final List<TipoIntervalo> tiposIntervalos) {
        final Map<Long, TipoIntervalo> tiposIntervalosMap = new HashMap<>();
        tiposIntervalos.forEach(tipoIntervalo -> tiposIntervalosMap.put(tipoIntervalo.getCodigo(), tipoIntervalo));
        return tiposIntervalosMap;
    }
}