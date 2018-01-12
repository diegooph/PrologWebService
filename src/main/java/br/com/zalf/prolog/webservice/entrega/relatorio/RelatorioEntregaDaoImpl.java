package br.com.zalf.prolog.webservice.entrega.relatorio;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.entrega.indicador.Indicador;
import br.com.zalf.prolog.webservice.entrega.indicador.IndicadorDao;
import br.com.zalf.prolog.webservice.entrega.indicador.IndicadorDaoImpl;
import br.com.zalf.prolog.webservice.entrega.indicador.acumulado.IndicadorAcumulado;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jean on 13/09/16.
 */
public class RelatorioEntregaDaoImpl extends DatabaseConnection implements RelatorioEntregaDao {

    private static final String TAG = RelatorioEntregaDaoImpl.class.getSimpleName();

    private static final String BUSCA_ACUMULADO_INDICADORES = "select\n" +
             IndicadorDaoImpl.COLUNAS_ACUMULADOS +
            " from mapa m join unidade_metas um on um.cod_unidade = m.cod_unidade\n" +
            "LEFT JOIN (SELECT t.mapa as tracking_mapa,\n" +
            "sum(case when t.disp_apont_cadastrado <= um.meta_raio_tracking then 1\n" +
            "else 0 end) as apontamentos_ok,\n" +
            "count(t.disp_apont_cadastrado) as total_apontamentos\n" +
            "from tracking t join unidade_metas um on um.cod_unidade = t.código_transportadora\n" +
            "group by 1) as tracking on tracking_mapa = m.mapa\n" +
            "JOIN UNIDADE_FUNCAO_PRODUTIVIDADE UFP ON UFP.COD_UNIDADE = M.COD_UNIDADE \n" +
            "JOIN colaborador C ON C.matricula_ambev = M.matricmotorista " +
            "AND C.COD_FUNCAO = UFP.COD_FUNCAO_MOTORISTA \n" +
            "JOIN EQUIPE E ON E.cod_unidade = M.cod_unidade AND C.cod_equipe = e.codigo\n" +
            "JOIN UNIDADE U ON U.codigo = M.cod_unidade\n" +
            "JOIN empresa EM ON EM.codigo = U.cod_empresa\n" +
            "JOIN regional R ON R.codigo = U.cod_regional\n" +
            "WHERE  M.DATA BETWEEN ? AND ? AND\n" +
            "EM.codigo::TEXT LIKE ? AND\n" +
            "R.codigo::TEXT LIKE ? AND\n" +
            "U.codigo::TEXT LIKE ? AND\n" +
            "E.nome LIKE ?\n" +
            "group by um.cod_unidade,um.meta_tracking,um.meta_tempo_rota_horas,um.meta_tempo_rota_mapas,um.meta_caixa_viagem,\n" +
            "um.meta_dev_hl,um.meta_dev_pdv,um.meta_dispersao_km,um.meta_dispersao_tempo,um.meta_jornada_liquida_horas,\n" +
            "um.meta_jornada_liquida_mapas,um.meta_raio_tracking,um.meta_tempo_interno_horas,um.meta_tempo_interno_mapas,um.meta_tempo_largada_horas,\n" +
            "um.meta_tempo_largada_mapas, um.meta_dev_nf;";

    private static final String BUSCA_ACUMULADO_POR_DIA = "select m.data,\n" +
            IndicadorDaoImpl.COLUNAS_ACUMULADOS +
            " from mapa m join unidade_metas um on um.cod_unidade = m.cod_unidade\n" +
            "LEFT JOIN (SELECT t.mapa as tracking_mapa,\n" +
            "sum(case when t.disp_apont_cadastrado <= um.meta_raio_tracking then 1\n" +
            "else 0 end) as apontamentos_ok,\n" +
            "count(t.disp_apont_cadastrado) as total_apontamentos\n" +
            "from tracking t join unidade_metas um on um.cod_unidade = t.código_transportadora\n" +
            "group by 1) as tracking on tracking_mapa = m.mapa\n" +
            "JOIN UNIDADE_FUNCAO_PRODUTIVIDADE UFP ON UFP.COD_UNIDADE = M.COD_UNIDADE \n" +
            "JOIN colaborador C ON C.matricula_ambev = M.matricmotorista\n" +
            " AND C.COD_FUNCAO = UFP.COD_FUNCAO_MOTORISTA \n" +
            "JOIN EQUIPE E ON E.cod_unidade = m.cod_unidade AND C.cod_equipe = e.codigo\n" +
            "JOIN UNIDADE U ON U.codigo = M.cod_unidade\n" +
            "JOIN empresa EM ON EM.codigo = U.cod_empresa\n" +
            "JOIN regional R ON R.codigo = U.cod_regional\n" +
            "WHERE  M.DATA BETWEEN ? AND ? AND\n" +
            " EM.codigo::TEXT LIKE ? AND\n" +
            " R.codigo::TEXT LIKE ? AND\n" +
            " U.codigo::TEXT LIKE ? AND\n" +
            "E.nome LIKE ?\n" +
            "group by 1, um.cod_unidade,um.meta_tracking,um.meta_tempo_rota_horas,um.meta_tempo_rota_mapas,um.meta_caixa_viagem,\n" +
            "um.meta_dev_hl,um.meta_dev_pdv,um.meta_dispersao_km,um.meta_dispersao_tempo,um.meta_jornada_liquida_horas,\n" +
            "um.meta_jornada_liquida_mapas,um.meta_raio_tracking,um.meta_tempo_interno_horas,um.meta_tempo_interno_mapas,um.meta_tempo_largada_horas,\n" +
            "um.meta_tempo_largada_mapas,um.meta_dev_nf\n" +
            "ORDER BY 1 %s;";
    
    public static final String FRAGMENTO_BUSCA_EXTRATO_DIA = IndicadorDaoImpl.COLUNAS_EXTRATO +
            " FROM MAPA M \n" +
            "JOIN UNIDADE_FUNCAO_PRODUTIVIDADE UFP ON UFP.COD_UNIDADE = M.COD_UNIDADE \n" +
            "JOIN colaborador c1 on c1.matricula_ambev = m.matricmotorista and c1.cod_unidade = m.cod_unidade\n" +
            "AND C1.COD_FUNCAO = UFP.COD_FUNCAO_MOTORISTA " +
            "JOIN UNIDADE U ON U.CODIGO = M.cod_unidade\n" +
            "JOIN EMPRESA EM ON EM.codigo = U.cod_empresa\n" +
            "JOIN regional R ON R.codigo = U.cod_regional\n" +
            "JOIN unidade_metas um on um.cod_unidade = u.codigo\n" +
            "JOIN equipe E ON E.cod_unidade = U.codigo AND C1.cod_equipe = E.codigo AND C1.cod_unidade = E.cod_unidade\n" +
            "LEFT JOIN (SELECT t.mapa as tracking_mapa, \n"+
            "sum(case when t.disp_apont_cadastrado <= um.meta_raio_tracking then 1 \n"+
            "else 0 end) as apontamentos_ok, \n"+
            "count(t.disp_apont_cadastrado) as total_apontamentos \n"+
            "from tracking t join unidade_metas um on um.cod_unidade = t.código_transportadora \n"+
            "group by 1) as tracking ON TRACKING_MAPA = M.MAPA \n" +
            "LEFT JOIN colaborador c2 on c2.matricula_ambev = m.matricajud1 and c2.cod_unidade = m.cod_unidade and c2.cod_funcao = ufp.cod_funcao_ajudante\n" +
            "LEFT JOIN colaborador c3 on c3.matricula_ambev = m.matricajud2 and c3.cod_unidade = m.cod_unidade and c3.cod_funcao = ufp.cod_funcao_ajudante ";

    public RelatorioEntregaDaoImpl() {

    }

    @Override
    public List<IndicadorAcumulado> getAcumuladoIndicadores(Long dataInicial, Long dataFinal, String codEmpresa,
                                                            String codRegional, String codUnidade, String equipe) throws SQLException {
        Connection conn = null;
        ResultSet rSet = null;
        PreparedStatement stmt = null;
        List<IndicadorAcumulado> acumulados = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(BUSCA_ACUMULADO_INDICADORES);
            stmt.setDate(1, DateUtils.toSqlDate(new Date(dataInicial)));
            stmt.setDate(2, DateUtils.toSqlDate(new Date(dataFinal)));
            stmt.setString(3, codEmpresa);
            stmt.setString(4, codRegional);
            stmt.setString(5, codUnidade);
            stmt.setString(6, equipe);
            rSet = stmt.executeQuery();
            final IndicadorDao indicadorDao = Injection.provideIndicadorDao();
            // TODO: poderia ser um if aqui?
            while (rSet.next()) {
                acumulados = indicadorDao.createAcumulados(rSet);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return acumulados;
    }

    @Override
    public List<Indicador> getExtratoIndicador(Long dataInicial, Long dataFinal, String codRegional, String codEmpresa,
                                               String codUnidade, String equipe, String cpf, String indicador) throws SQLException {
        final IndicadorDao indicadorDao = Injection.provideIndicadorDao();
        return indicadorDao.getExtratoIndicador(dataInicial, dataFinal, codRegional, codEmpresa,
                codUnidade, equipe, cpf, indicador);
    }

    @Override
    public List<ConsolidadoDia> getConsolidadoDia(Long dataInicial, Long dataFinal, String codEmpresa,
                                                  String codRegional, String codUnidade, String equipe, int limit, int offset) throws SQLException {
        Connection conn = null;
        ResultSet rSet = null;
        PreparedStatement stmt = null;
        List<ConsolidadoDia> consolidados = new ArrayList<>();
        try {
            conn = getConnection();
            String query = String.format(BUSCA_ACUMULADO_POR_DIA, " LIMIT " + limit + " OFFSET " + offset);
            stmt = conn.prepareStatement(query);
            stmt.setDate(1, DateUtils.toSqlDate(new Date(dataInicial)));
            stmt.setDate(2, DateUtils.toSqlDate(new Date(dataFinal)));
            stmt.setString(3, codEmpresa);
            stmt.setString(4, codRegional);
            stmt.setString(5, codUnidade);
            stmt.setString(6, equipe);
            rSet = stmt.executeQuery();
            final IndicadorDao indicadorDao = Injection.provideIndicadorDao();
            while (rSet.next()) {
                final ConsolidadoDia consolidado = new ConsolidadoDia();
                consolidado.setData(rSet.getDate("DATA"))
                        .setQtdMapas(rSet.getInt("VIAGENS_TOTAL"))
                        .setIndicadores(indicadorDao.createAcumulados(rSet));
                consolidados.add(consolidado);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return consolidados;
    }

    @Override
    public List<MapaEstratificado> getMapasEstratificados(Long data, String codEmpresa, String codRegional,
                                                          String codUnidade, String equipe) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<MapaEstratificado> mapas = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT \n" +
                    FRAGMENTO_BUSCA_EXTRATO_DIA +
                    " WHERE\n" +
                    "m.DATA = ? AND\n" +
                    "EM.codigo::TEXT LIKE ? AND\n" +
                    "R.codigo::TEXT LIKE ? AND\n" +
                    "U.codigo::TEXT LIKE ? AND\n" +
                    "E.nome::TEXT LIKE ? \n" +
                    "ORDER BY M.MAPA;");
            stmt.setDate(1, DateUtils.toSqlDate(new Date(data)));
            stmt.setString(2, codEmpresa);
            stmt.setString(3, codRegional);
            stmt.setString(4, codUnidade);
            stmt.setString(5, equipe);
            Log.d(TAG, stmt.toString());
            rSet = stmt.executeQuery();
            final IndicadorDao indicadorDao = Injection.provideIndicadorDao();
            while (rSet.next()) {
                final MapaEstratificado mapa = new MapaEstratificado();
                mapa.setNumeroMapa(rSet.getInt("MAPA"))
                        .setMotorista(rSet.getString("MOTORISTA"))
                        .setAjudante1(rSet.getString("AJ1"))
                        .setAjudante2(rSet.getString("AJ2"))
                        .setData(rSet.getDate("DATA"))
                        .setPlaca(rSet.getString("PLACA"))
                        .setEquipe(rSet.getString("EQUIPE"))
                        .setIndicadores(indicadorDao.createExtratoDia(rSet));
                mapas.add(mapa);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return mapas;
    }

    @Override
    public List<DadosGrafico> getDadosGrafico(Long dataInicial, Long dataFinal, String codEmpresa,
                                              String codRegional, String codUnidade, String equipe, String indicador)
            throws SQLException {
        Connection conn = null;
        ResultSet rSet = null;
        PreparedStatement stmt = null;
        List<DadosGrafico> dados = new ArrayList<>();
        try {
            conn = getConnection();
            String query = String.format(BUSCA_ACUMULADO_POR_DIA, "");
            stmt = conn.prepareStatement(query);
            stmt.setDate(1, DateUtils.toSqlDate(new Date(dataInicial)));
            stmt.setDate(2, DateUtils.toSqlDate(new Date(dataFinal)));
            stmt.setString(3, codEmpresa);
            stmt.setString(4, codRegional);
            stmt.setString(5, codUnidade);
            stmt.setString(6, equipe);
            rSet = stmt.executeQuery();
            final IndicadorDao indicadorDao = Injection.provideIndicadorDao();
            while (rSet.next()) {
                final DadosGrafico dado = new DadosGrafico();
                dado.setData(rSet.getDate("DATA"))
                        .setIndicador(indicadorDao.createAcumuladoIndicador(rSet, indicador));
                dados.add(dado);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return dados;
    }

    @Override
    public void getEstratificacaoMapasCsv(Long codUnidade, Date dataInicial, Date dataFinal, OutputStream out)
            throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoMapasStatement(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getEstratificacaoMapasReport(Long codUnidade, Date dataInicial, Date dataFinal) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getEstratificacaoMapasStatement(conn, codUnidade, dataInicial, dataFinal);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    private PreparedStatement getEstratificacaoMapasStatement(Connection conn, Long codUnidade, Date dataInicial,
                                                              Date dataFinal) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM func_relatorio_mapa_estratificado(?,?,?);");
        stmt.setLong(1, codUnidade);
        stmt.setDate(2, DateUtils.toSqlDate(dataInicial));
        stmt.setDate(3, DateUtils.toSqlDate(dataFinal));
        return stmt;
    }

    @Override
    public void getExtratoMapasIndicadorCsv(Long codEmpresa, String codRegional, String codUnidade, String cpf,
                                            Date dataInicial, Date dataFinal, String equipe, OutputStream out) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getExtratoMapasIndicadorStatement(codEmpresa, codRegional, codUnidade, cpf, dataInicial, dataFinal, equipe, conn);
            rSet = stmt.executeQuery();
            new CsvWriter().write(rSet, out);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Override
    public Report getExtratoMapasIndicadorReport(Long codEmpresa, String codRegional, String codUnidade, String cpf,
                                                 Date dataInicial, Date dataFinal, String equipe) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = getExtratoMapasIndicadorStatement(codEmpresa, codRegional, codUnidade, cpf, dataInicial, dataFinal, equipe, conn);
            rSet = stmt.executeQuery();
            return ReportTransformer.createReport(rSet);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @NotNull
    private PreparedStatement getExtratoMapasIndicadorStatement(Long codEmpresa, String codRegional, String codUnidade, String cpf,
                                                                Date dataInicial, Date dataFinal, String equipe, Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("select * from func_relatorio_extrato_mapas_indicadores(?,?,?,?,?,?,?);");
        stmt.setDate(1, DateUtils.toSqlDate(dataInicial));
        stmt.setDate(2, DateUtils.toSqlDate(dataFinal));
        stmt.setString(3, cpf);
        stmt.setString(4, codUnidade);
        stmt.setString(5, equipe);
        stmt.setLong(6, codEmpresa);
        stmt.setString(7, codRegional);
        return stmt;
    }
}