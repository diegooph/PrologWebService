package br.com.zalf.prolog.webservice.entrega.indicador;

import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.entrega.indicador.indicadores.acumulado.IndicadorAcumulado;
import br.com.zalf.prolog.entrega.indicador.indicadores.item.*;
import br.com.zalf.prolog.webservice.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IndicadorDaoImpl extends DatabaseConnection{

	private static final String BUSCA_INDICADORES = "SELECT DISTINCT\n" +
			"  M.DATA,  M.mapa,M.cxcarreg,    M.QTHLCARREGADOS,  M.QTHLENTREGUES,  M.entregascompletas,  M.entregasnaorealizadas,\n" +
			"  M.kmprevistoroad, M.kmsai, M.kmentr, M.tempoprevistoroad,\n" +
			"  M.HRSAI,  M.HRENTR, (M.hrentr - M.hrsai)::time AS TEMPO_ROTA,  M.TEMPOINTERNO,  M.HRMATINAL,  TRACKING.TOTAL AS TOTAL_TRACKING,  TRACKING.APONTAMENTO_OK, um.* \n" +
			"FROM\n" +
			"  MAPA_COLABORADOR MC\n" +
			"  JOIN COLABORADOR C ON C.COD_UNIDADE = MC.COD_UNIDADE AND MC.COD_AMBEV = C.MATRICULA_AMBEV\n" +
			"  JOIN MAPA M ON M.MAPA = MC.MAPA\n" +
			"  JOIN UNIDADE U ON U.CODIGO = M.cod_unidade\n" +
			"  JOIN EMPRESA EM ON EM.codigo = U.cod_empresa\n" +
			"  JOIN regional R ON R.codigo = U.cod_regional\n" +
			"  JOIN unidade_metas um on um.cod_unidade = u.codigo\n" +
			"  JOIN equipe E ON E.cod_unidade = U.codigo AND C.cod_equipe = E.codigo AND C.cod_unidade = E.cod_unidade\n" +
			"  LEFT JOIN (SELECT t.mapa AS TRACKING_MAPA, total.total AS TOTAL, ok.APONTAMENTOS_OK AS APONTAMENTO_OK\n" +
			"              FROM tracking t\n" +
			"                JOIN mapa_colaborador mc ON mc.mapa = t.mapa\n" +
			"                JOIN (SELECT t.mapa AS mapa_ok, count(t.disp_apont_cadastrado) AS apontamentos_ok\n" +
			"                FROM tracking t\n" +
			"                  JOIN unidade_metas um on um.cod_unidade = t.código_transportadora\n" +
			"                WHERE t.disp_apont_cadastrado <= um.meta_raio_tracking\n" +
			"                GROUP BY t.mapa) AS ok ON mapa_ok = t.mapa\n" +
			"            JOIN (SELECT t.mapa AS total_entregas, count(t.cod_cliente) AS total\n" +
			"             FROM tracking t\n" +
			"             GROUP BY t.mapa) AS total ON total_entregas = t.mapa\n" +
			"             GROUP BY t.mapa, OK.APONTAMENTOS_OK, total.total) AS TRACKING ON TRACKING_MAPA = M.MAPA\n" +
			"WHERE\n" +
			"  DATA BETWEEN ? AND ? AND\n" +
			"  R.codigo::TEXT LIKE ? AND\n" +
			"  U.codigo::TEXT LIKE ? AND\n" +
			"  E.codigo::TEXT LIKE ? AND\n" +
			"  EM.codigo = ? AND\n" +
			"  C.CPF::TEXT LIKE ? \n" +
			"ORDER BY M.DATA;\n";


	public List<Indicador> getEstratoIndicador(Long dataInicial, Long dataFinal, String codRegional, Long codEmpresa,
											   String codUnidade, String equipe, String cpf, String indicador) throws SQLException {

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Indicador> itens = new ArrayList<>();

		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_INDICADORES);
			stmt.setDate(1, DateUtils.toSqlDate(new Date(dataInicial)));
			stmt.setDate(2, DateUtils.toSqlDate(new Date(dataFinal)));
			stmt.setString(3, codRegional);
			stmt.setString(4, codUnidade);
			stmt.setString(5, equipe);
			stmt.setLong(6, codEmpresa);
			stmt.setString(7, cpf);
			rSet = stmt.executeQuery();
			itens = createExtratoIndicador(rSet, indicador);

		} finally {
			closeConnection(conn, stmt, rSet);
		}
		System.out.println(itens);
		return itens;
	}

	private List<Indicador> createExtratoIndicador(ResultSet rSet, String indicador) throws SQLException{

		if (indicador.equals(CaixaViagem.CAIXA_VIAGEM)){
			return Converter.createExtratoCaixaViagem(rSet);
		}else if(indicador.equals(DevHl.DEVOLUCAO_HL)){
			return Converter.createExtratoDevHl(rSet);
		}else if(indicador.equals(DevPdv.DEVOLUCAO_PDV)){
			return Converter.createExtratoDevPdv(rSet);
		}else if(indicador.equals(DispersaoKm.DISPERSAO_KM)){
			return Converter.createExtratoDispersaoKm(rSet);
		}else if(indicador.equals(Tracking.TRACKING)){
			return Converter.createExtratoTracking(rSet);
		}else if(indicador.equals(DispersaoTempo.DISPERSAO_TEMPO)){
			return Converter.createExtratoDispersaoTempo(rSet);
		}else if(indicador.equals(Jornada.JORNADA)){
			return Converter.createExtratoJornada(rSet);
		}else if(indicador.equals(TempoInterno.TEMPO_INTERNO)){
			return Converter.createExtratoTempoInterno(rSet);
		}else if(indicador.equals(TempoLargada.TEMPO_LARGADA)){
			return Converter.createExtratoTempoLargada(rSet);
		}else if(indicador.equals(TempoRota.TEMPO_ROTA)) {
			return Converter.createExtratoTempoRota(rSet);
		}
		return new ArrayList<>();
	}

	public List<IndicadorAcumulado> getAcumuladoIndicadores()throws SQLException{
		Connection conn = null;
		ResultSet rSet = null;
		PreparedStatement stmt = null;
		List<IndicadorAcumulado> acumulados = new ArrayList<>();
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("select\n" +
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
					"\tJOIN mapa_colaborador MC ON MC.mapa = M.mapa AND MC.cod_unidade = M.cod_unidade\n" +
					"\tJOIN colaborador C ON C.cod_unidade = MC.cod_unidade AND C.matricula_ambev = MC.cod_ambev\n" +
					"\tJOIN UNIDADE U ON U.codigo = M.cod_unidade\n" +
					"\tJOIN empresa EM ON EM.codigo = U.cod_empresa\n" +
					"\tJOIN regional R ON R.codigo = U.cod_regional\n" +
					"\tJOIN equipe E ON E.codigo = C.cod_equipe\n" +
					"group by um.cod_unidade,um.meta_tracking,um.meta_tempo_rota_horas,um.meta_tempo_rota_mapas,um.meta_caixa_viagem,\n" +
					"\tum.meta_dev_hl,um.meta_dev_pdv,um.meta_dispersao_km,um.meta_dispersao_tempo,um.meta_jornada_liquida_horas,\n" +
					"\tum.meta_jornada_liquida_mapas,um.meta_raio_tracking,um.meta_tempo_interno_horas,um.meta_tempo_interno_mapas,um.meta_tempo_largada_horas,\n" +
					"\tum.meta_tempo_largada_mapas;");
			rSet = stmt.executeQuery();
			acumulados = createAcumulados(rSet);
		}finally {
			closeConnection(conn,stmt,rSet);
		}
		return acumulados;
	}

	private List<IndicadorAcumulado> createAcumulados(ResultSet rSet) throws SQLException{
		List<IndicadorAcumulado> acumulados = new ArrayList<>();
		if(rSet.next()) {
			acumulados.add(Converter.createAcumuladoCaixaViagem(rSet));
			acumulados.add(Converter.createAcumuladoDevHl(rSet));
			acumulados.add(Converter.createAcumuladoDevPdv(rSet));
			acumulados.add(Converter.createAcumuladoDispersaoKm(rSet));
			acumulados.add(Converter.createAcumuladoDispersaoTempoMapas(rSet));
			acumulados.add(Converter.createAcumuladoDispersaoTempoMedia(rSet));
			acumulados.add(Converter.createAcumuladoJornadaMapas(rSet));
			acumulados.add(Converter.createAcumuladoJornadaMedia(rSet));
			acumulados.add(Converter.createAcumuladoTempoInternoMapas(rSet));
			acumulados.add(Converter.createAcumuladoTempoInternoMedia(rSet));
		}
		return acumulados;
	}
}
