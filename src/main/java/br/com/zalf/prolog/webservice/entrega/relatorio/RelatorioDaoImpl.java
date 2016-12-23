package br.com.zalf.prolog.webservice.entrega.relatorio;

import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.entrega.indicador.indicadores.Indicador;
import br.com.zalf.prolog.entrega.indicador.indicadores.acumulado.IndicadorAcumulado;
import br.com.zalf.prolog.entrega.relatorio.ConsolidadoDia;
import br.com.zalf.prolog.entrega.relatorio.DadosGrafico;
import br.com.zalf.prolog.entrega.relatorio.MapaEstratificado;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.entrega.indicador.IndicadorDaoImpl;
import br.com.zalf.prolog.webservice.util.L;

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
public class RelatorioDaoImpl extends DatabaseConnection{

    private static final String TAG = RelatorioDaoImpl.class.getSimpleName();

    private static final String BUSCA_ACUMULADO_INDICADORES = "select\n" +
             IndicadorDaoImpl.COLUNAS_ACUMULADOS +
            " from mapa m join unidade_metas um on um.cod_unidade = m.cod_unidade\n" +
            "LEFT JOIN (SELECT t.mapa as tracking_mapa,\n" +
            "sum(case when t.disp_apont_cadastrado <= um.meta_raio_tracking then 1\n" +
            "else 0 end) as apontamentos_ok,\n" +
            "count(t.disp_apont_cadastrado) as total_apontamentos\n" +
            "from tracking t join unidade_metas um on um.cod_unidade = t.código_transportadora\n" +
            "group by 1) as tracking on tracking_mapa = m.mapa\n" +
            "JOIN colaborador C ON C.matricula_ambev = M.matricmotorista\n" +
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
            "JOIN colaborador C ON C.matricula_ambev = M.matricmotorista\n" +
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
            " FROM \n" +
            "MAPA M \n" +
            "JOIN colaborador c1 on c1.matricula_ambev = m.matricmotorista and c1.cod_unidade = m.cod_unidade\n" +
            "JOIN UNIDADE U ON U.CODIGO = M.cod_unidade\n" +
            "JOIN EMPRESA EM ON EM.codigo = U.cod_empresa\n" +
            "JOIN regional R ON R.codigo = U.cod_regional\n" +
            "JOIN unidade_metas um on um.cod_unidade = u.codigo\n" +
            "JOIN equipe E ON E.cod_unidade = U.codigo AND C1.cod_equipe = E.codigo AND C1.cod_unidade = E.cod_unidade\n" +
            "LEFT JOIN (SELECT t.mapa AS TRACKING_MAPA, total.total AS TOTAL, ok.APONTAMENTOS_OK AS APONTAMENTO_OK\n" +
            "FROM tracking t\n" +
            "JOIN mapa_colaborador mc ON mc.mapa = t.mapa\n" +
            "JOIN (SELECT t.mapa AS mapa_ok, count(t.disp_apont_cadastrado) AS apontamentos_ok\n" +
            "FROM tracking t\n" +
            "JOIN unidade_metas um on um.cod_unidade = t.código_transportadora\n" +
            "WHERE t.disp_apont_cadastrado <= um.meta_raio_tracking\n" +
            "GROUP BY t.mapa) AS ok ON mapa_ok = t.mapa\n" +
            "JOIN (SELECT t.mapa AS total_entregas, count(t.cod_cliente) AS total\n" +
            "FROM tracking t\n" +
            "GROUP BY t.mapa) AS total ON total_entregas = t.mapa\n" +
            "GROUP BY t.mapa, OK.APONTAMENTOS_OK, total.total) AS TRACKING ON TRACKING_MAPA = M.MAPA\n" +
            "LEFT JOIN colaborador c2 on c2.matricula_ambev = m.matricajud1 and c2.cod_unidade = m.cod_unidade\n" +
            "LEFT JOIN colaborador c3 on c3.matricula_ambev = m.matricajud2 and c3.cod_unidade = m.cod_unidade ";

    /**
     * Método utilizado para buscar os dados da aba acumulados, tela Relatórios.
     * Busca os dados pós clique de um card da aba Diário, mostrando o acumulado
     * do dia clicado, tela Relatórios, pilar Entrega.
     * @param dataInicial um Long
     * @param dataFinal um Long
     * @param codEmpresa código da empresa a ser usado no filtro
     * @param codRegional código da regional a ser usado no filtro
     * @param codUnidade código da unidade a ser usado no filtro
     * @param equipe nome da equipe a ser usado no filtro
     * @return lista de {@link IndicadorAcumulado}
     * @throws SQLException caso não seja possível realizar a busca
     */
    public List<IndicadorAcumulado> getAcumuladoIndicadores(Long dataInicial, Long dataFinal, String codEmpresa,
                                                            String codRegional, String codUnidade, String equipe)throws SQLException{
        Connection conn = null;
        ResultSet rSet = null;
        PreparedStatement stmt = null;
        List<IndicadorAcumulado> acumulados = new ArrayList<>();
        try{
            conn = getConnection();
            stmt = conn.prepareStatement(BUSCA_ACUMULADO_INDICADORES);
            stmt.setDate(1, DateUtils.toSqlDate(new Date(dataInicial)));
            stmt.setDate(2, DateUtils.toSqlDate(new Date(dataFinal)));
            stmt.setString(3, codEmpresa);
            stmt.setString(4, codRegional);
            stmt.setString(5, codUnidade);
            stmt.setString(6, equipe);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                acumulados = new IndicadorDaoImpl().createAcumulados(rSet);
            }
        }finally {
            closeConnection(conn,stmt,rSet);
        }
        return acumulados;
    }


    public List<Indicador> getExtratoIndicador(Long dataInicial, Long dataFinal, String codRegional, String codEmpresa,
                                               String codUnidade, String equipe, String cpf, String indicador) throws SQLException {

        return new IndicadorDaoImpl().getExtratoIndicador(dataInicial, dataFinal, codRegional, codEmpresa,
                codUnidade, equipe, cpf, indicador);
    }

    /**
     * Busca os dados para a aba Diário da tela Relatórios, pilar Entrega
     * @param dataInicial um Long
     * @param dataFinal um Long
     * @param codEmpresa código da empresa usado no filtro
     * @param codRegional código da regional usado no filtro
     * @param codUnidade código da unidade usado no filtro
     * @param equipe nome da equipe usado no filtro
     * @return lista de {@link ConsolidadoDia}
     * @throws SQLException caso não seja possível realizar a busca
     */
    public List<ConsolidadoDia> getConsolidadoDia(Long dataInicial, Long dataFinal, String codEmpresa,
                                                  String codRegional, String codUnidade, String equipe, int limit, int offset)throws SQLException{
        Connection conn = null;
        ResultSet rSet = null;
        PreparedStatement stmt = null;
        List<ConsolidadoDia> consolidados = new ArrayList<>();
        try{
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
            IndicadorDaoImpl indicadorDao = new IndicadorDaoImpl();
            while (rSet.next()){
                ConsolidadoDia consolidado = new ConsolidadoDia();
                consolidado.setData(rSet.getDate("DATA"))
                            .setQtdMapas(rSet.getInt("VIAGENS_TOTAL"))
                            .setIndicadores(indicadorDao.createAcumulados(rSet));
                consolidados.add(consolidado);
            }
        }finally {
            closeConnection(conn,stmt,rSet);
        }
        return consolidados;
    }

    /**
     * Estratifica os mapas de um dia, contém os dados da equipe, data e mapa, além dos indicadores
     * no formato de item e não de acumulado, chamado quando acontece um clique no FAB
     * @param data uma data, serão buscados apenas os mapas dessa data
     * @param codEmpresa código da empresa a ser usado no filtro
     * @param codRegional código da regional a ser usado no filtro
     * @param codUnidade código da unidade a ser usado no filtro
     * @param equipe nome da equipe a ser usado no filtro
     * @return lista de {@link MapaEstratificado}
     * @throws SQLException caso não seja possível realizar a busca
     */
    public List<MapaEstratificado> getMapasEstratificados(Long data, String codEmpresa, String codRegional,
                                                          String codUnidade, String equipe) throws SQLException{
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<MapaEstratificado> mapas = new ArrayList<>();
        IndicadorDaoImpl indicadorDao = new IndicadorDaoImpl();
        try{
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
            stmt.setString(4 ,codUnidade);
            stmt.setString(5, equipe);
            L.d(TAG, stmt.toString());
            rSet = stmt.executeQuery();
            while(rSet.next()){
                MapaEstratificado mapa = new MapaEstratificado();
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
        }finally {
            closeConnection(conn,stmt,rSet);
        }
        return mapas;
    }

    public List<DadosGrafico> getDadosGrafico(Long dataInicial, Long dataFinal, String codEmpresa,
                                                String codRegional, String codUnidade, String equipe, String indicador)throws SQLException{
        Connection conn = null;
        ResultSet rSet = null;
        PreparedStatement stmt = null;
        List<DadosGrafico> dados = new ArrayList<>();
        try{
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
            IndicadorDaoImpl indicadorDao = new IndicadorDaoImpl();
            while (rSet.next()){
                DadosGrafico dado = new DadosGrafico();
                dado.setData(rSet.getDate("DATA"))
                        .setIndicador(indicadorDao.createAcumuladoIndicador(rSet, indicador));
                dados.add(dado);
            }
        }finally {
            closeConnection(conn,stmt,rSet);
        }
        return dados;
    }
}
