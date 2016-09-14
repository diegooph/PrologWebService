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

	private static final String TAG = IndicadorDaoImpl.class.getSimpleName();

	private static final String BUSCA_EXTRATO_INDICADORES = "SELECT DISTINCT\n" +
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
			"  E.nome::TEXT LIKE ? AND\n" +
			"  EM.codigo = ? AND\n" +
			"  C.CPF::TEXT LIKE ? \n" +
			"ORDER BY M.DATA;\n";

	private static final String BUSCA_ACUMULADO_INDICADORES_INDIVIDUAL = "select " +
			"-- CaixaViagem \n" +
			"  sum(m.cxcarreg) as carregadas_total, count(m.mapa) as viagens_total, \n" +
			"-- Dev Hl \n"+
			"sum(m.qthlcarregados) hl_carregados_total, sum(qthlcarregados - qthlentregues) as hl_devolvidos_total,\n"  +
			"  -- Dev Pdvn  \n"+
			"  sum(m.entregascompletas + m.entregasnaorealizadas) as pdv_carregados_total, sum(m.entregasnaorealizadas) as pdv_devolvidos_total,\n"+
			"  -- Dispersão Km \n " +
			"sum(case when (kmentr - m.kmsai) > 0 and (kmentr - m.kmsai) < 2000 then m.kmprevistoroad \n" +
			"else 0 end) as km_planejado_total,\n"+
			"sum(case when (kmentr - m.kmsai) > 0 and (kmentr - m.kmsai) < 2000 then (m.kmentr - m.kmsai) \n"  +
			"else 0 end) as km_percorrido_total,\n"+
			"  -- Dispersão de tempo  \n"+
			"sum(case when (m.hrentr - m.hrsai) <= m.tempoprevistoroad and (m.hrentr - m.hrsai) > '00:00' and m.tempoprevistoroad > '00:00' then 1 \n"  +
			"else 0 \n" +
			"end) as total_mapas_bateram_dispersao_tempo,\n" +
			"avg(case WHEN (m.hrentr - m.hrsai) > '00:00'  and m.tempoprevistoroad > '00:00' then (m.hrentr - m.hrsai)\n"  +
			"end)::time as media_dispersao_tempo_realizado,\n"  +
			"avg(case WHEN (m.hrentr - m.hrsai) > '00:00'  and m.tempoprevistoroad > '00:00' then m.tempoprevistoroad \n"  +
			"end)::time as media_dispersao_tempo_planejado,\n"  +
			"  -- Jornada --  primeiro verifica se é >00:00, depois verifica se é menor do que a meta \n"  +
			"sum(case when \n"  +
			" (case when m.hrsai::time < m.hrmatinal then (um.meta_tempo_largada_horas::timetz + (m.hrentr - m.hrsai) + m.tempointerno) \n"  +
			"when m.hrsai::time >= m.hrmatinal then ((m.hrsai::time - m.hrmatinal) + (m.hrentr - m.hrsai) + m.tempointerno) \n" +
			"end) > '00:00' \n"  +
			"and \n"  +
			"(case when m.hrsai::time < m.hrmatinal then (um.meta_tempo_largada_horas::timetz + (m.hrentr - m.hrsai) + m.tempointerno) \n"  +
			"when m.hrsai::time >= m.hrmatinal then ((m.hrsai::time - m.hrmatinal) + (m.hrentr - m.hrsai) + m.tempointerno) \n"  +
			"end) <= um.meta_jornada_liquida_horas then 1 else 0 end) as total_mapas_bateram_jornada,\n"  +
			" sum(case when m.hrsai::time < m.hrmatinal then (um.meta_tempo_largada_horas::interval + (m.hrentr - m.hrsai) + m.tempointerno)\n" +
			"when m.hrsai::time >= m.hrmatinal then ((m.hrsai::time - m.hrmatinal) + (m.hrentr - m.hrsai) + m.tempointerno)\n"  +
			" else null \n"  +
			"end)::time as media_jornada, \n"  +
			"  --Tempo Interno \n"  +
			"sum(case when m.tempointerno <= um.meta_tempo_interno_horas and m.tempointerno > '00:00' then 1 \n"  +
			"else 0 \n"  +
			"end) as total_mapas_bateram_tempo_interno, \n"  +
			"sum(case when m.tempointerno <= '05:00' and m.tempointerno > '00:00' then 1 \n"  +
			"else 0 \n"  +
			"end) as total_mapas_validos_tempo_interno, \n"  +
			"avg(case when m.tempointerno > '00:00' and m.tempointerno <= '05:00' then m.tempointerno \n"  +
			"else null \n"  +
			"end)::time as media_tempo_interno, \n"  +
			"  -- Tempo largada \n" +
			"sum(case when \n"  +
			"(case when m.hrsai::time < m.hrmatinal then um.meta_tempo_largada_horas \n"  +
			"else (m.hrsai - m.hrmatinal)::time \n"  +
			"end) <= um.meta_tempo_largada_horas then 1 \n"  +
			"else 0 end) as total_mapas_bateram_tempo_largada, \n"  +
			"sum(case when \n"  +
			"(case when m.hrsai::time < m.hrmatinal then um.meta_tempo_largada_horas \n"  +
			"else (m.hrsai - m.hrmatinal)::time \n"  +
			"end) <= '05:00' then 1 \n"  +
			"else 0 end) as total_mapas_validos_tempo_largada, \n"  +
			"avg(case when m.hrsai::time < m.hrmatinal then um.meta_tempo_largada_horas \n"  +
			"when (m.hrsai - m.hrmatinal)::time > '05:00' then '00:30' \n"  +
			"else (m.hrsai - m.hrmatinal)::time \n"  +
			"end)::time media_tempo_largada, \n"  +
			"  -- Tempo Rota \n"  +
			"sum(case when (m.hrentr - m.hrsai) > '00:00' and (m.hrentr - m.hrsai) <= meta_tempo_rota_horas then 1 \n"  +
			"else 0 end) as total_mapas_bateram_tempo_rota, \n"  +
			"avg(case when (m.hrentr - m.hrsai) > '00:00' then (m.hrentr - m.hrsai) \n"  +
			"end)::time as media_tempo_rota, \n"  +
			"  -- Tracking \n"  +
			"sum(tracking.apontamentos_ok) as total_apontamentos_ok, \n"  +
			"sum(tracking.total_apontamentos) as total_apontamentos, \n"  +
			"um.*n \n" +
			"from mapa m join unidade_metas um on um.cod_unidade = m.cod_unidade \n"  +
			"LEFT JOIN (SELECT t.mapa as tracking_mapa, \n"  +
			"sum(case when t.disp_apont_cadastrado <= um.meta_raio_tracking then 1 \n"  +
			"else 0 end) as apontamentos_ok, \n"  +
			"count(t.disp_apont_cadastrado) as total_apontamentos \n"  +
			"from tracking t join unidade_metas um on um.cod_unidade = t.código_transportadora \n"  +
			"group by 1) as tracking on tracking_mapa = m.mapa \n"  +
			"JOIN mapa_colaborador MC ON MC.mapa = M.mapa AND MC.cod_unidade = M.cod_unidade \n"  +
			"JOIN colaborador C ON C.cod_unidade = MC.cod_unidade AND C.matricula_ambev = MC.cod_ambev \n"  +
			"JOIN UNIDADE U ON U.codigo = M.cod_unidade \n"  +
			"WHERE  M.DATA BETWEEN ? AND ? AND \n"  +
			"  C.CPF = ? \n"  +
			"group by um.cod_unidade,um.meta_tracking,um.meta_tempo_rota_horas,um.meta_tempo_rota_mapas,um.meta_caixa_viagem, "  +
			"um.meta_dev_hl,um.meta_dev_pdv,um.meta_dispersao_km,um.meta_dispersao_tempo,um.meta_jornada_liquida_horas, "  +
			"um.meta_jornada_liquida_mapas,um.meta_raio_tracking,um.meta_tempo_interno_horas,um.meta_tempo_interno_mapas,um.meta_tempo_largada_horas, "  +
			"um.meta_tempo_largada_mapas;";


	/**
	 * Busca o extrato por mapa de qualquer indicador, usa distinct para não repetir os mapas
	 * @param dataInicial uma data
	 * @param dataFinal uma data
	 * @param codRegional código da Regional ou '%'
	 * @param codEmpresa código da Empresa ou '%'
	 * @param codUnidade código da Unidade ou '%'
	 * @param equipe nome da equipe ou '%'
	 * @param cpf cpf ou '%'
	 * @param indicador constante provinda dos ITENS  ex: {@link Jornada#JORNADA}
     * @return uma lista de Indicador {@link Indicador}
     * @throws SQLException caso não seja possível realizar a busca no BD
     */
	public List<Indicador> getExtratoIndicador(Long dataInicial, Long dataFinal, String codRegional, Long codEmpresa,
											   String codUnidade, String equipe, String cpf, String indicador) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Indicador> itens = new ArrayList<>();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_EXTRATO_INDICADORES);
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
		return itens;
	}

	/**
	 * Cria o extrato acumulado INDIVIDUAL dos indicadores, serve apenas para a tela indicadores
	 * @param dataInicial uma data
	 * @param dataFinal uma data
	 * @param cpf colaborador ao qual será realizada a busca
	 * @return uma lista de {@link IndicadorAcumulado}
	 * @throws SQLException caso não seja possível realizar a busca
     */
	public List<IndicadorAcumulado> getAcumuladoIndicadoresIndividual(Long dataInicial, Long dataFinal, Long cpf)throws SQLException{
		Connection conn = null;
		ResultSet rSet = null;
		PreparedStatement stmt = null;
		List<IndicadorAcumulado> acumulados = new ArrayList<>();
		try{
			conn = getConnection();
			stmt = conn.prepareStatement(BUSCA_ACUMULADO_INDICADORES_INDIVIDUAL);
			stmt.setDate(1, DateUtils.toSqlDate(new Date(dataInicial)));
			stmt.setDate(2, DateUtils.toSqlDate(new Date(dataFinal)));
			stmt.setLong(3, cpf);
			rSet = stmt.executeQuery();
			if(rSet.next()) {
				acumulados = createAcumulados(rSet);
			}
		}finally {
			closeConnection(conn,stmt,rSet);
		}
		return acumulados;
	}

	/**
	 * Cria os objetos que representam os indicadores acumulados
	 * @param rSet ResultSet contendo os acumulados do período solicitado
	 * @return uma lista de {@link IndicadorAcumulado}
	 * @throws SQLException caso não seja possível recuperar alguma coluna do ResultSet
     */
	public List<IndicadorAcumulado> createAcumulados(ResultSet rSet) throws SQLException{
		List<IndicadorAcumulado> acumulados = new ArrayList<>();
			acumulados.add(IndicadorConverter.createAcumuladoCaixaViagem(rSet));
			acumulados.add(IndicadorConverter.createAcumuladoDevHl(rSet));
			acumulados.add(IndicadorConverter.createAcumuladoDevPdv(rSet));
			acumulados.add(IndicadorConverter.createAcumuladoDispersaoKm(rSet));
			acumulados.add(IndicadorConverter.createAcumuladoDispersaoTempoMapas(rSet));
			acumulados.add(IndicadorConverter.createAcumuladoDispersaoTempoMedia(rSet));
			acumulados.add(IndicadorConverter.createAcumuladoJornadaMapas(rSet));
			acumulados.add(IndicadorConverter.createAcumuladoJornadaMedia(rSet));
			acumulados.add(IndicadorConverter.createAcumuladoTempoInternoMapas(rSet));
			acumulados.add(IndicadorConverter.createAcumuladoTempoInternoMedia(rSet));
			acumulados.add(IndicadorConverter.createAcumuladoTempoLargadaMapas(rSet));
			acumulados.add(IndicadorConverter.createAcumuladoTempoLargadaMedia(rSet));
			acumulados.add(IndicadorConverter.createAcumuladoTempoRotaMapas(rSet));
			acumulados.add(IndicadorConverter.createAcumuladoTempoRotaMedia(rSet));
			acumulados.add(IndicadorConverter.createAcumuladoTracking(rSet));
		return acumulados;
	}

	/**
	 * Cria os itens para compor o extrato
	 * @param rSet um ResultSet contendo o resultado da busca
	 * @param indicador String para indicar qual o indicador que deve ser montado o extrato
	 * @return uma lista de Indicador {@link Indicador}
	 * @throws SQLException caso não seja possível recuparar alguma coluna do ResultSet
	 */
	public List<Indicador> createExtratoIndicador(ResultSet rSet, String indicador) throws SQLException{

		if (indicador.equals(CaixaViagem.CAIXA_VIAGEM)){
			return IndicadorConverter.createExtratoCaixaViagem(rSet);
		}else if(indicador.equals(DevHl.DEVOLUCAO_HL)){
			return IndicadorConverter.createExtratoDevHl(rSet);
		}else if(indicador.equals(DevPdv.DEVOLUCAO_PDV)){
			return IndicadorConverter.createExtratoDevPdv(rSet);
		}else if(indicador.equals(DispersaoKm.DISPERSAO_KM)){
			return IndicadorConverter.createExtratoDispersaoKm(rSet);
		}else if(indicador.equals(Tracking.TRACKING)){
			return IndicadorConverter.createExtratoTracking(rSet);
		}else if(indicador.equals(DispersaoTempo.DISPERSAO_TEMPO)){
			return IndicadorConverter.createExtratoDispersaoTempo(rSet);
		}else if(indicador.equals(Jornada.JORNADA)){
			return IndicadorConverter.createExtratoJornada(rSet);
		}else if(indicador.equals(TempoInterno.TEMPO_INTERNO)){
			return IndicadorConverter.createExtratoTempoInterno(rSet);
		}else if(indicador.equals(TempoLargada.TEMPO_LARGADA)){
			return IndicadorConverter.createExtratoTempoLargada(rSet);
		}else if(indicador.equals(TempoRota.TEMPO_ROTA)) {
			return IndicadorConverter.createExtratoTempoRota(rSet);
		}
		return new ArrayList<>();
	}
}
