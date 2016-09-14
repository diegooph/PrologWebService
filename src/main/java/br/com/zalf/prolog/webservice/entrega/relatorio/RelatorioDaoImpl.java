package br.com.zalf.prolog.webservice.entrega.relatorio;

import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.entrega.indicador.indicadores.ConsolidadoDia;
import br.com.zalf.prolog.entrega.indicador.indicadores.Indicador;
import br.com.zalf.prolog.entrega.indicador.indicadores.acumulado.IndicadorAcumulado;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.entrega.indicador.IndicadorDaoImpl;

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
            "  -- CaixaViagem\n" +
            "  sum(m.cxcarreg) as carregadas_total, count(m.mapa) as viagens_total,\n" +
            "\n" +
            "\t-- Dev Hl\n" +
            "  sum(m.qthlcarregados) hl_carregados_total, sum(qthlcarregados - qthlentregues) as hl_devolvidos_total,\n" +
            "  -- Dev Pdv\n" +
            "  sum(m.entregascompletas + m.entregasnaorealizadas) as pdv_carregados_total, sum(m.entregasnaorealizadas) as pdv_devolvidos_total,\n" +
            "\n" +
            "  -- Dispersão Km\n" +
            "\tsum(case when (kmentr - m.kmsai) > 0 and (kmentr - m.kmsai) < 2000 then m.kmprevistoroad\n" +
            "\t\t\telse 0 end) as km_planejado_total,\n" +
            "\n" +
            "\tsum(case when (kmentr - m.kmsai) > 0 and (kmentr - m.kmsai) < 2000 then (m.kmentr - m.kmsai)\n" +
            "\t\t\telse 0 end) as km_percorrido_total,\n" +
            "\n" +
            "  -- Dispersão de tempo\n" +
            "\tsum(case when (m.hrentr - m.hrsai) <= m.tempoprevistoroad and (m.hrentr - m.hrsai) > '00:00' and m.tempoprevistoroad > '00:00' then 1\n" +
            "\t\t\t\t\t\t\t\telse 0\n" +
            "\t\t\t\t\t\t\t\tend) as total_mapas_bateram_dispersao_tempo,\n" +
            "\tavg(case WHEN (m.hrentr - m.hrsai) > '00:00'  and m.tempoprevistoroad > '00:00' then (m.hrentr - m.hrsai)\n" +
            "\t\tend)::time as media_dispersao_tempo_realizado,\n" +
            "\tavg(case WHEN (m.hrentr - m.hrsai) > '00:00'  and m.tempoprevistoroad > '00:00' then m.tempoprevistoroad\n" +
            "\t\tend)::time as media_dispersao_tempo_planejado,\n" +
            "\n" +
            "  -- Jornada --  primeiro verifica se é >00:00, depois verifica se é menor do que a meta\n" +
            "sum(case when\n" +
            "\t\t\t\t\t\t\t (case when m.hrsai::time < m.hrmatinal then (um.meta_tempo_largada_horas::timetz + (m.hrentr - m.hrsai) + m.tempointerno)\n" +
            "\t\t\t\t\t\t\t\twhen m.hrsai::time >= m.hrmatinal then ((m.hrsai::time - m.hrmatinal) + (m.hrentr - m.hrsai) + m.tempointerno)\n" +
            "\t\t\t\t\t\t\t\tend) > '00:00'\n" +
            "\t\t\t\t\tand\n" +
            "\t\t\t\t\t\t\t\t(case when m.hrsai::time < m.hrmatinal then (um.meta_tempo_largada_horas::timetz + (m.hrentr - m.hrsai) + m.tempointerno)\n" +
            "\t\t\t\t\t\t\t\twhen m.hrsai::time >= m.hrmatinal then ((m.hrsai::time - m.hrmatinal) + (m.hrentr - m.hrsai) + m.tempointerno)\n" +
            "\t\t\t\t\t\t\t\tend) <= um.meta_jornada_liquida_horas then 1 else 0 end) as total_mapas_bateram_jornada,\n" +
            " sum(case when m.hrsai::time < m.hrmatinal then (um.meta_tempo_largada_horas::interval + (m.hrentr - m.hrsai) + m.tempointerno)\n" +
            "\t\twhen m.hrsai::time >= m.hrmatinal then ((m.hrsai::time - m.hrmatinal) + (m.hrentr - m.hrsai) + m.tempointerno)\n" +
            "\t else null\n" +
            "\t\tend)::time as media_jornada,\n" +
            "  --Tempo Interno\n" +
            "\tsum(case when m.tempointerno <= um.meta_tempo_interno_horas and m.tempointerno > '00:00' then 1\n" +
            "\t\t\telse 0\n" +
            "\t\t\tend) as total_mapas_bateram_tempo_interno,\n" +
            "\n" +
            "\tsum(case when m.tempointerno <= '05:00' and m.tempointerno > '00:00' then 1\n" +
            "\t\t\telse 0\n" +
            "\t\t\tend) as total_mapas_validos_tempo_interno,\n" +
            "\n" +
            "\tavg(case when m.tempointerno > '00:00' and m.tempointerno <= '05:00' then m.tempointerno\n" +
            "\t\t\telse null\n" +
            "\t\t\tend)::time as media_tempo_interno,\n" +
            "\n" +
            "  -- Tempo largada\n" +
            "\tsum(case when\n" +
            "\t\t\t\t\t(case when m.hrsai::time < m.hrmatinal then um.meta_tempo_largada_horas\n" +
            "\t\t\t\t\telse (m.hrsai - m.hrmatinal)::time\n" +
            "\t\t\t\t\tend) <= um.meta_tempo_largada_horas then 1\n" +
            "\t\telse 0 end)\tas total_mapas_bateram_tempo_largada,\n" +
            "\n" +
            "\t\tsum(case when\n" +
            "\t\t\t\t\t(case when m.hrsai::time < m.hrmatinal then um.meta_tempo_largada_horas\n" +
            "\t\t\t\t\telse (m.hrsai - m.hrmatinal)::time\n" +
            "\t\t\t\t\tend) <= '05:00' then 1\n" +
            "\t\telse 0 end)\tas total_mapas_validos_tempo_largada,\n" +
            "\n" +
            "\tavg(case when m.hrsai::time < m.hrmatinal then um.meta_tempo_largada_horas\n" +
            "\t\twhen (m.hrsai - m.hrmatinal)::time > '05:00' then '00:30'\n" +
            "\t\t\telse (m.hrsai - m.hrmatinal)::time\n" +
            "\t\t\tend)::time media_tempo_largada,\n" +
            "\n" +
            "  -- Tempo Rota\n" +
            "\tsum(case when (m.hrentr - m.hrsai) > '00:00' and (m.hrentr - m.hrsai) <= meta_tempo_rota_horas then 1\n" +
            "\t\telse 0 end)\tas total_mapas_bateram_tempo_rota,\n" +
            "\n" +
            "\tavg(case when (m.hrentr - m.hrsai) > '00:00' then (m.hrentr - m.hrsai)\n" +
            "\t\tend)::time\tas media_tempo_rota,\n" +
            "\n" +
            "  -- Tracking\n" +
            "sum(tracking.apontamentos_ok) as total_apontamentos_ok,\n" +
            "\tsum(tracking.total_apontamentos) as total_apontamentos,\n" +
            "\tum.*\n" +
            "from mapa m join unidade_metas um on um.cod_unidade = m.cod_unidade\n" +
            "\t\t\t\t\t\t\tLEFT JOIN (SELECT t.mapa as tracking_mapa,\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\tsum(case when t.disp_apont_cadastrado <= um.meta_raio_tracking then 1\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\telse 0 end) as apontamentos_ok,\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\tcount(t.disp_apont_cadastrado) as total_apontamentos\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\tfrom tracking t join unidade_metas um on um.cod_unidade = t.código_transportadora\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\tgroup by 1) as tracking on tracking_mapa = m.mapa\n" +
            "\n" +
            "\tJOIN colaborador C ON C.matricula_ambev = M.matricmotorista\n" +
            "\tJOIN EQUIPE E ON E.cod_unidade = M.cod_unidade AND C.cod_equipe = e.codigo\n" +
            "\tJOIN UNIDADE U ON U.codigo = M.cod_unidade\n" +
            "\tJOIN empresa EM ON EM.codigo = U.cod_empresa\n" +
            "\tJOIN regional R ON R.codigo = U.cod_regional\n" +
            "\tWHERE  M.DATA BETWEEN ? AND ? AND\n" +
            "\t\t\t\t EM.codigo = ? AND\n" +
            "\t\t\t\t R.codigo::TEXT LIKE ? AND\n" +
            "\t\t\t\t U.codigo::TEXT LIKE ? AND\n" +
            "\t\t\t\t\tE.nome LIKE ?\n" +
            "group by um.cod_unidade,um.meta_tracking,um.meta_tempo_rota_horas,um.meta_tempo_rota_mapas,um.meta_caixa_viagem,\n" +
            "\tum.meta_dev_hl,um.meta_dev_pdv,um.meta_dispersao_km,um.meta_dispersao_tempo,um.meta_jornada_liquida_horas,\n" +
            "\tum.meta_jornada_liquida_mapas,um.meta_raio_tracking,um.meta_tempo_interno_horas,um.meta_tempo_interno_mapas,um.meta_tempo_largada_horas,\n" +
            "\tum.meta_tempo_largada_mapas;";

    private static final String BUSCA_AUMULADO_POR_DIA = "select m.data,\n" +
            "  -- CaixaViagem\n" +
            "  sum(m.cxcarreg) as carregadas_total, count(m.mapa) as viagens_total,\n" +
            "\n" +
            "\t-- Dev Hl\n" +
            "  sum(m.qthlcarregados) hl_carregados_total, sum(qthlcarregados - qthlentregues) as hl_devolvidos_total,\n" +
            "  -- Dev Pdv\n" +
            "  sum(m.entregascompletas + m.entregasnaorealizadas) as pdv_carregados_total, sum(m.entregasnaorealizadas) as pdv_devolvidos_total,\n" +
            "\n" +
            "  -- Dispersão Km\n" +
            "\tsum(case when (kmentr - m.kmsai) > 0 and (kmentr - m.kmsai) < 2000 then m.kmprevistoroad\n" +
            "\t\t\telse 0 end) as km_planejado_total,\n" +
            "\n" +
            "\tsum(case when (kmentr - m.kmsai) > 0 and (kmentr - m.kmsai) < 2000 then (m.kmentr - m.kmsai)\n" +
            "\t\t\telse 0 end) as km_percorrido_total,\n" +
            "\n" +
            "  -- Dispersão de tempo\n" +
            "\tsum(case when (m.hrentr - m.hrsai) <= m.tempoprevistoroad and (m.hrentr - m.hrsai) > '00:00' and m.tempoprevistoroad > '00:00' then 1\n" +
            "\t\t\t\t\t\t\t\telse 0\n" +
            "\t\t\t\t\t\t\t\tend) as total_mapas_bateram_dispersao_tempo,\n" +
            "\tavg(case WHEN (m.hrentr - m.hrsai) > '00:00'  and m.tempoprevistoroad > '00:00' then (m.hrentr - m.hrsai)\n" +
            "\t\tend)::time as media_dispersao_tempo_realizado,\n" +
            "\tavg(case WHEN (m.hrentr - m.hrsai) > '00:00'  and m.tempoprevistoroad > '00:00' then m.tempoprevistoroad\n" +
            "\t\tend)::time as media_dispersao_tempo_planejado,\n" +
            "\n" +
            "  -- Jornada --  primeiro verifica se é >00:00, depois verifica se é menor do que a meta\n" +
            "sum(case when\n" +
            "\t\t\t\t\t\t\t (case when m.hrsai::time < m.hrmatinal then (um.meta_tempo_largada_horas::timetz + (m.hrentr - m.hrsai) + m.tempointerno)\n" +
            "\t\t\t\t\t\t\t\twhen m.hrsai::time >= m.hrmatinal then ((m.hrsai::time - m.hrmatinal) + (m.hrentr - m.hrsai) + m.tempointerno)\n" +
            "\t\t\t\t\t\t\t\tend) > '00:00'\n" +
            "\t\t\t\t\tand\n" +
            "\t\t\t\t\t\t\t\t(case when m.hrsai::time < m.hrmatinal then (um.meta_tempo_largada_horas::timetz + (m.hrentr - m.hrsai) + m.tempointerno)\n" +
            "\t\t\t\t\t\t\t\twhen m.hrsai::time >= m.hrmatinal then ((m.hrsai::time - m.hrmatinal) + (m.hrentr - m.hrsai) + m.tempointerno)\n" +
            "\t\t\t\t\t\t\t\tend) <= um.meta_jornada_liquida_horas then 1 else 0 end) as total_mapas_bateram_jornada,\n" +
            " sum(case when m.hrsai::time < m.hrmatinal then (um.meta_tempo_largada_horas::interval + (m.hrentr - m.hrsai) + m.tempointerno)\n" +
            "\t\twhen m.hrsai::time >= m.hrmatinal then ((m.hrsai::time - m.hrmatinal) + (m.hrentr - m.hrsai) + m.tempointerno)\n" +
            "\t else null\n" +
            "\t\tend)::time as media_jornada,\n" +
            "  --Tempo Interno\n" +
            "\tsum(case when m.tempointerno <= um.meta_tempo_interno_horas and m.tempointerno > '00:00' then 1\n" +
            "\t\t\telse 0\n" +
            "\t\t\tend) as total_mapas_bateram_tempo_interno,\n" +
            "\n" +
            "\tsum(case when m.tempointerno <= '05:00' and m.tempointerno > '00:00' then 1\n" +
            "\t\t\telse 0\n" +
            "\t\t\tend) as total_mapas_validos_tempo_interno,\n" +
            "\n" +
            "\tavg(case when m.tempointerno > '00:00' and m.tempointerno <= '05:00' then m.tempointerno\n" +
            "\t\t\telse null\n" +
            "\t\t\tend)::time as media_tempo_interno,\n" +
            "\n" +
            "  -- Tempo largada\n" +
            "\tsum(case when\n" +
            "\t\t\t\t\t(case when m.hrsai::time < m.hrmatinal then um.meta_tempo_largada_horas\n" +
            "\t\t\t\t\telse (m.hrsai - m.hrmatinal)::time\n" +
            "\t\t\t\t\tend) <= um.meta_tempo_largada_horas then 1\n" +
            "\t\telse 0 end)\tas total_mapas_bateram_tempo_largada,\n" +
            "\n" +
            "\t\tsum(case when\n" +
            "\t\t\t\t\t(case when m.hrsai::time < m.hrmatinal then um.meta_tempo_largada_horas\n" +
            "\t\t\t\t\telse (m.hrsai - m.hrmatinal)::time\n" +
            "\t\t\t\t\tend) <= '05:00' then 1\n" +
            "\t\telse 0 end)\tas total_mapas_validos_tempo_largada,\n" +
            "\n" +
            "\tavg(case when m.hrsai::time < m.hrmatinal then um.meta_tempo_largada_horas\n" +
            "\t\twhen (m.hrsai - m.hrmatinal)::time > '05:00' then '00:30'\n" +
            "\t\t\telse (m.hrsai - m.hrmatinal)::time\n" +
            "\t\t\tend)::time media_tempo_largada,\n" +
            "\n" +
            "  -- Tempo Rota\n" +
            "\tsum(case when (m.hrentr - m.hrsai) > '00:00' and (m.hrentr - m.hrsai) <= meta_tempo_rota_horas then 1\n" +
            "\t\telse 0 end)\tas total_mapas_bateram_tempo_rota,\n" +
            "\n" +
            "\tavg(case when (m.hrentr - m.hrsai) > '00:00' then (m.hrentr - m.hrsai)\n" +
            "\t\tend)::time\tas media_tempo_rota,\n" +
            "\n" +
            "  -- Tracking\n" +
            "sum(tracking.apontamentos_ok) as total_apontamentos_ok,\n" +
            "\tsum(tracking.total_apontamentos) as total_apontamentos,\n" +
            "\tum.*\n" +
            "from mapa m join unidade_metas um on um.cod_unidade = m.cod_unidade\n" +
            "\t\t\t\t\t\t\tLEFT JOIN (SELECT t.mapa as tracking_mapa,\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\tsum(case when t.disp_apont_cadastrado <= um.meta_raio_tracking then 1\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\telse 0 end) as apontamentos_ok,\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\tcount(t.disp_apont_cadastrado) as total_apontamentos\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\tfrom tracking t join unidade_metas um on um.cod_unidade = t.código_transportadora\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\tgroup by 1) as tracking on tracking_mapa = m.mapa\n" +
            "\tJOIN colaborador C ON C.matricula_ambev = M.matricmotorista\n" +
            "\tJOIN EQUIPE E ON E.cod_unidade = m.cod_unidade AND C.cod_equipe = e.codigo\n" +
            "\tJOIN UNIDADE U ON U.codigo = M.cod_unidade\n" +
            "\tJOIN empresa EM ON EM.codigo = U.cod_empresa\n" +
            "\tJOIN regional R ON R.codigo = U.cod_regional\n" +
            "\tWHERE  M.DATA BETWEEN ? AND ? AND\n" +
            "\t\t\t\t EM.codigo = ? AND\n" +
            "\t\t\t\t R.codigo::TEXT LIKE ? AND\n" +
            "\t\t\t\t U.codigo::TEXT LIKE ? AND\n" +
            "\t\t\t\t\tE.nome LIKE ?\n" +
            "group by 1, um.cod_unidade,um.meta_tracking,um.meta_tempo_rota_horas,um.meta_tempo_rota_mapas,um.meta_caixa_viagem,\n" +
            "\tum.meta_dev_hl,um.meta_dev_pdv,um.meta_dispersao_km,um.meta_dispersao_tempo,um.meta_jornada_liquida_horas,\n" +
            "\tum.meta_jornada_liquida_mapas,um.meta_raio_tracking,um.meta_tempo_interno_horas,um.meta_tempo_interno_mapas,um.meta_tempo_largada_horas,\n" +
            "\tum.meta_tempo_largada_mapas\n" +
            "ORDER BY 1;";

    public List<IndicadorAcumulado> getAcumuladoIndicadores(Long dataInicial, Long dataFinal, Long codEmpresa,
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
            stmt.setLong(3, codEmpresa);
            stmt.setString(4, codRegional);
            stmt.setString(5, codUnidade);
            stmt.setString(6, equipe);
            rSet = stmt.executeQuery();
            acumulados = new IndicadorDaoImpl().createAcumulados(rSet);
        }finally {
            closeConnection(conn,stmt,rSet);
        }
        return acumulados;
    }

    public List<Indicador> getExtratoIndicador(Long dataInicial, Long dataFinal, String codRegional, Long codEmpresa,
                                               String codUnidade, String equipe, String cpf, String indicador) throws SQLException {

        return new IndicadorDaoImpl().getExtratoIndicador(dataInicial, dataFinal, codRegional, codEmpresa,
                codUnidade, equipe, cpf, indicador);
    }

    public List<ConsolidadoDia> getConsolidadoDia(Long dataInicial, Long dataFinal, Long codEmpresa,
                                                  String codRegional, String codUnidade, String equipe)throws SQLException{
        Connection conn = null;
        ResultSet rSet = null;
        PreparedStatement stmt = null;
        List<ConsolidadoDia> consolidados = new ArrayList<>();
        try{
            conn = getConnection();
            stmt = conn.prepareStatement(BUSCA_AUMULADO_POR_DIA);
            stmt.setDate(1, DateUtils.toSqlDate(new Date(dataInicial)));
            stmt.setDate(2, DateUtils.toSqlDate(new Date(dataFinal)));
            stmt.setLong(3, codEmpresa);
            stmt.setString(4, codRegional);
            stmt.setString(5, codUnidade);
            stmt.setString(6, equipe);
            rSet = stmt.executeQuery();
            IndicadorDaoImpl indicadorDao = new IndicadorDaoImpl();
            while (rSet.next()){
                ConsolidadoDia consolidado = new ConsolidadoDia();
                consolidado.setData(rSet.getDate("DATA"));
                consolidado.setQtdMapas(rSet.getInt("VIAGENS_TOTAL"));
                consolidado.setIndicadores(indicadorDao.createAcumulados(rSet));
                consolidados.add(consolidado);
            }
        }finally {
            closeConnection(conn,stmt,rSet);
        }
        return consolidados;
    }
}
