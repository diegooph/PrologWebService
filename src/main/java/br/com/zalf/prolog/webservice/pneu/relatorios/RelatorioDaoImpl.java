package br.com.zalf.prolog.webservice.pneu.relatorios;

import br.com.zalf.prolog.commons.Report;
import br.com.zalf.prolog.commons.util.DateUtils;
import br.com.zalf.prolog.frota.pneu.Pneu;
import br.com.zalf.prolog.frota.pneu.Restricao;
import br.com.zalf.prolog.frota.pneu.relatorio.Aderencia;
import br.com.zalf.prolog.frota.pneu.relatorio.Faixa;
import br.com.zalf.prolog.frota.pneu.relatorio.ResumoServicos;
import br.com.zalf.prolog.frota.pneu.servico.Servico;
import br.com.zalf.prolog.webservice.CsvWriter;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDaoImpl;
import br.com.zalf.prolog.webservice.pneu.afericao.AfericaoDao;
import br.com.zalf.prolog.webservice.pneu.afericao.AfericaoDaoImpl;
import br.com.zalf.prolog.webservice.pneu.pneu.PneuDao;
import br.com.zalf.prolog.webservice.pneu.pneu.PneuDaoImpl;
import br.com.zalf.prolog.webservice.report.ReportConverter;
import br.com.zalf.prolog.webservice.util.L;
import br.com.zalf.prolog.webservice.util.PostgresUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Classe responsável por estratificar os dados dos pneus.
 * @author jean
 *
 */
public class RelatorioDaoImpl extends DatabaseConnection implements RelatorioDao {

	private static final String TAG = RelatorioDaoImpl.class.getSimpleName();

	private static final String PNEUS_RESUMO_SULCOS="SELECT COALESCE(ALTURA_SULCO_CENTRAL, ALTURA_SULCO_CENTRAL, -1) AS ALTURA_SULCO_CENTRAL FROM PNEU WHERE "
			+ "COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND STATUS LIKE ANY (ARRAY[?])  ORDER BY 1 DESC";

	private static final String SULCOS_PNEUS_BY_FAIXAS = "SELECT MP.NOME AS MARCA, MP.CODIGO AS COD_MARCA, P.CODIGO, P.PRESSAO_ATUAL, P.VIDA_ATUAL, "
			+ "P.VIDA_TOTAL, MOP.NOME AS MODELO, MOP.CODIGO AS COD_MODELO,PD.CODIGO AS COD_DIMENSAO, PD.ALTURA, PD.LARGURA, PD.ARO, P.PRESSAO_RECOMENDADA, "
			+ "P.altura_sulcos_novos,P.altura_sulco_CENTRAL, P.altura_sulco_INTERNO, P.altura_sulco_EXTERNO, p.status "
			+ "FROM PNEU P "
			+ "JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO "
			+ "JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA "
			+ "JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO "
			+ "JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE "
			+ "JOIN EMPRESA E ON E.CODIGO = U.COD_EMPRESA "
			+ "WHERE P.ALTURA_SULCO_CENTRAL >= ? AND P.ALTURA_SULCO_CENTRAL < ? AND E.CODIGO = ? AND P.COD_UNIDADE::TEXT LIKE ? "
			+ "ORDER BY P.ALTURA_SULCO_CENTRAL DESC "
			+ "LIMIT ? OFFSET ?";

	private static final String RESUMO_SERVICOS = "SELECT AD.DATA, CAL.CAL_ABERTAS, INSP.INSP_ABERTAS, MOV.MOV_ABERTAS, CAL_FECHADAS.CAL_FECHADAS, INSP_FECHADAS.INSP_FECHADAS, MOV_FECHADAS.MOV_FECHADAS FROM AUX_DATA AD LEFT JOIN "
			+ "-- BUSCA AS CALIBRAGENS ABERTAS \n "
			+ "(SELECT A.DATA_HORA::DATE AS DATA, COUNT(A.CODIGO) AS CAL_ABERTAS FROM "
			+ "AFERICAO A JOIN AFERICAO_MANUTENCAO AM ON A.CODIGO = AM.COD_AFERICAO "
			+ "WHERE "
			+ "COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND AM.TIPO_SERVICO LIKE ? "
			+ "GROUP BY A.DATA_HORA::DATE "
			+ "ORDER BY A.DATA_HORA::DATE DESC) AS CAL ON CAL.DATA = AD.DATA "
			+ "LEFT JOIN "
			+ "-- BUSCA AS INSPEÇÕES ABERTAS \n "
			+ "(SELECT A.DATA_HORA::DATE AS DATA, COUNT(A.CODIGO) AS INSP_ABERTAS FROM "
			+ "AFERICAO A JOIN AFERICAO_MANUTENCAO AM ON A.CODIGO = AM.COD_AFERICAO "
			+ "WHERE  "
			+ "COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND AM.TIPO_SERVICO LIKE ? "
			+ "GROUP BY A.DATA_HORA::DATE "
			+ "ORDER BY A.DATA_HORA::DATE DESC) AS INSP ON INSP.DATA = AD.DATA "
			+ "LEFT JOIN "
			+ "-- BUSCA AS MOVIMENTAÇÕES ABERTAS \n "
			+ "(SELECT AM.DATA_HORA_RESOLUCAO::DATE AS DATA, COUNT(A.CODIGO) AS MOV_ABERTAS FROM "
			+ "AFERICAO A JOIN AFERICAO_MANUTENCAO AM ON A.CODIGO = AM.COD_AFERICAO "
			+ "WHERE  "
			+ "COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND AM.TIPO_SERVICO LIKE ? "
			+ "GROUP BY AM.DATA_HORA_RESOLUCAO::DATE "
			+ "ORDER BY AM.DATA_HORA_RESOLUCAO::DATE DESC) AS MOV ON MOV.DATA = AD.DATA "
			+ "LEFT JOIN "
			+ "-- BUSCA AS CALIBRAGENS FECHADAS \n "
			+ "(SELECT AM.DATA_HORA_RESOLUCAO::DATE AS DATA, COUNT(A.CODIGO) AS CAL_FECHADAS FROM "
			+ "AFERICAO A JOIN AFERICAO_MANUTENCAO AM ON A.CODIGO = AM.COD_AFERICAO "
			+ "WHERE AM.DATA_HORA_RESOLUCAO IS NOT NULL AND "
			+ "COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND AM.TIPO_SERVICO LIKE ? "
			+ "GROUP BY AM.DATA_HORA_RESOLUCAO::DATE "
			+ "ORDER BY AM.DATA_HORA_RESOLUCAO::DATE DESC) AS CAL_FECHADAS ON CAL_FECHADAS.DATA = AD.DATA "
			+ "LEFT JOIN "
			+ "-- BUSCA AS INSPEÇÕES FECHADAS \n "
			+ "(SELECT AM.DATA_HORA_RESOLUCAO::DATE AS DATA, COUNT(A.CODIGO) AS INSP_FECHADAS FROM "
			+ "AFERICAO A JOIN AFERICAO_MANUTENCAO AM ON A.CODIGO = AM.COD_AFERICAO "
			+ "WHERE AM.DATA_HORA_RESOLUCAO IS NOT NULL AND "
			+ "COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND AM.TIPO_SERVICO LIKE ? "
			+ "GROUP BY AM.DATA_HORA_RESOLUCAO::DATE "
			+ "ORDER BY AM.DATA_HORA_RESOLUCAO::DATE DESC) AS INSP_FECHADAS ON INSP_FECHADAS.DATA = AD.DATA "
			+ "LEFT JOIN "
			+ "-- BUSCA AS MOVIMENTAÇÕES FECHADAS \n "
			+ "(SELECT AM.DATA_HORA_RESOLUCAO::DATE AS DATA, COUNT(A.CODIGO) AS MOV_FECHADAS FROM "
			+ "AFERICAO A JOIN AFERICAO_MANUTENCAO AM ON A.CODIGO = AM.COD_AFERICAO "
			+ "WHERE AM.DATA_HORA_RESOLUCAO IS NOT NULL AND "
			+ "COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND AM.TIPO_SERVICO LIKE ? "
			+ "GROUP BY AM.DATA_HORA_RESOLUCAO::DATE "
			+ "ORDER BY AM.DATA_HORA_RESOLUCAO::DATE DESC) AS MOV_FECHADAS ON MOV_FECHADAS.DATA = AD.DATA "
			+ "WHERE AD.DATA BETWEEN ? AND ? ";


	@Override
	public List<Faixa> getQtPneusByFaixaSulco(List<String> codUnidades, List<String> status)throws SQLException {

		List<Double> valores = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try{
			conn = getConnection();
			stmt = conn.prepareStatement(PNEUS_RESUMO_SULCOS);
			stmt.setArray(1, PostgresUtil.ListToArray(conn, codUnidades));
			stmt.setArray(2, PostgresUtil.ListToArray(conn, status));
			rSet = stmt.executeQuery();
			while(rSet.next()){
				valores.add(rSet.getDouble("ALTURA_SULCO_CENTRAL"));
			}			
		}finally{
			closeConnection(conn, stmt, rSet);
		}
		if (valores.isEmpty()) {
			return new ArrayList<>();

		}else{
			return getFaixas(valores);
		}
	};

	/**
	 * Busca os pneus respeitando os filtros de limite de sulcos aplicados, além do codEmpresa e codUnidade
	 * @param inicioFaixa menor sulco a ser considerado
	 * @param fimFaixa maior sulco a ser considerado
	 * @param codEmpresa
	 * @param codUnidade
	 * @return
	 * @throws SQLException
	 */
	@Override
	public List<Pneu> getPneusByFaixa(double inicioFaixa, double fimFaixa, Long codEmpresa, String codUnidade, long limit, long offset) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Pneu> pneus = new ArrayList<>();
		Pneu pneu = new Pneu();
		PneuDao pneuDao = new PneuDaoImpl();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(SULCOS_PNEUS_BY_FAIXAS);
			stmt.setDouble(1, inicioFaixa);
			stmt.setDouble(2, fimFaixa);
			stmt.setLong(3, codEmpresa);
			stmt.setString(4, String.valueOf(codUnidade));
			stmt.setLong(5, limit);
			stmt.setLong(6, offset);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				pneu = pneuDao.createPneu(rSet);
				pneus.add(pneu);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return pneus;
	}

	@Override
	public List<Aderencia> getAderenciaByUnidade(int ano, int mes, Long codUnidade) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		List<Aderencia> aderencias = new ArrayList<>();
		Aderencia aderencia = null;
		AfericaoDao afericaoDao = new AfericaoDaoImpl();
		VeiculoDao veiculoDao = new VeiculoDaoImpl();
		Restricao restricao = afericaoDao.getRestricoesByCodUnidade(codUnidade);

		Date dataAtual = new Date(System.currentTimeMillis());
		LocalDate dataInicial = LocalDate.of(ano, mes, 01);
		Date datainicial = Date.valueOf(dataInicial);

		double meta = 0;
		int totalVeiculos = 0;
		int ultimoDia = 0;
		int dia = 1;
		/* verifica se o mes procurado é o mesmo mes corrente, se for, pega o dia atual, caso contrário
		 pega o ultimo dia do mês */
		if (dataAtual.getYear()+1900 == ano && dataAtual.getMonth()+1 == mes) {
			ultimoDia = dataAtual.getDate();
		}else{
			ultimoDia = DateUtils.getUltimoDiaMes(datainicial).getDate();
		}

		try{
			conn = getConnection();
			totalVeiculos = veiculoDao.getTotalVeiculosByUnidade(codUnidade, conn);
			meta = totalVeiculos/restricao.getPeriodoDiasAfericao();
			stmt = conn.prepareStatement("SELECT EXTRACT(DAY from A.DATA_HORA) AS DIA, COUNT(EXTRACT(DAY from A.DATA_HORA)) AS REALIZADAS "
					+ "FROM AFERICAO A JOIN VEICULO V ON V.PLACA = A.PLACA_VEICULO "
					+ "WHERE A.DATA_HORA >=? AND A.DATA_HORA <= ? AND "
					+ "V.COD_UNIDADE = ? "
					+ "GROUP BY 1 "
					+ "ORDER BY 1");
			stmt.setDate(1, datainicial);
			stmt.setDate(2, DateUtils.toSqlDate(DateUtils.getUltimoDiaMes(datainicial)));
			stmt.setLong(3, codUnidade);
			rSet = stmt.executeQuery();
			while (rSet.next()) {
				while(dia < rSet.getInt("DIA")){
					aderencias.add(createAderencia(meta, dia));
					dia++;
				}
				aderencia = new Aderencia();
				aderencia.setDia(rSet.getInt("DIA"));
				aderencia.setRealizadas(rSet.getInt("REALIZADAS"));
				aderencia.setMeta(meta);
				aderencias.add(aderencia);
				dia++;

				if (rSet.isLast()) {
					while(dia <= ultimoDia){
						aderencias.add(createAderencia(meta, dia));
						dia++;
					}
				}
			}
		}finally{
			closeConnection(conn, stmt, rSet);
		}
		return aderencias;
	}

	@Override
	public List<Faixa> getQtPneusByFaixaPressao(List<String> codUnidades, List<String> status)throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Faixa> faixas = null;
		AfericaoDao afericaoDao = new AfericaoDaoImpl();
		if (!codUnidades.get(0).equals("%")) {
			Restricao restricao = afericaoDao.getRestricoesByCodUnidade(Long.parseLong(codUnidades.get(0)));
			Integer base = (int) Math.round(restricao.getToleranciaCalibragem()*100);
			faixas = criaFaixas(base, 30);
		}else{
			faixas = criaFaixas(0, 30);
		}
		List<Integer> valores = new ArrayList<>();
		int naoAferidos = 0;

		try{
			conn = getConnection();
			//			stmt = conn.prepareStatement("SELECT "
			//					+ "(((COALESCE(PRESSAO_ATUAL, PRESSAO_ATUAL, 0) - COALESCE(PRESSAO_RECOMENDADA, PRESSAO_RECOMENDADA, 0))/"
			//					+ "COALESCE(PRESSAO_RECOMENDADA, PRESSAO_RECOMENDADA, 0))*100)::INT AS PORC "
			//					+ "FROM PNEU "
			//					+ "WHERE COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND STATUS LIKE ANY (ARRAY[?])  "
			//					+ "ORDER BY 1 asc");

			stmt = conn.prepareStatement("SELECT COALESCE((((PRESSAO_ATUAL - PRESSAO_RECOMENDADA)/ PRESSAO_RECOMENDADA) *100)::TEXT, "
					+ "(((PRESSAO_ATUAL - PRESSAO_RECOMENDADA)/ PRESSAO_RECOMENDADA) *100)::TEXT, 'N') AS PORC  "
					+ "FROM PNEU  "
					+ "WHERE COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND STATUS LIKE ANY (ARRAY[?]) "
					+ "ORDER BY 1 asc");
			stmt.setArray(1, PostgresUtil.ListToArray(conn, codUnidades));
			stmt.setArray(2, PostgresUtil.ListToArray(conn, status));
			rSet = stmt.executeQuery();
			while(rSet.next()){
				if (rSet.getString("PORC").equals("N")) {
					naoAferidos ++;
				}else{
					valores.add((int) rSet.getDouble("PORC"));
				}
			}
		}finally{
			closeConnection(conn, stmt, rSet);
		}

		int totalValores = valores.size() + naoAferidos;
		L.d(TAG, "Total valores: " + totalValores);
		populaFaixas(faixas, valores);
		setPorcentagemFaixas(faixas, totalValores);
		Faixa faixa = new Faixa();
		faixa.setNaoAferidos(true);
		faixa.setTotalPneus(naoAferidos);
		faixa.setPorcentagem((double) naoAferidos/ (double) totalValores);
		faixas.add(faixa);
		L.d(TAG, "Finalizado: " + faixas.toString());
		return faixas;
	}

	@Override
	public List<ResumoServicos> getResumoServicosByUnidades(int ano, int mes, List<String> codUnidades) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		List<ResumoServicos> servicos = new ArrayList<>();
		ResumoServicos resumoDia = null;
		ResumoServicos.Servicos abertos = null;
		ResumoServicos.Servicos fechados = null;
		ResumoServicos.Servicos fechadosAc = null;
		ResumoServicos.Servicos abertosAc = null;

		Date dataInicial = new Date(ano-1900, mes-1, 01);

		try{
			conn = getConnection();
			stmt = conn.prepareStatement(RESUMO_SERVICOS);

			stmt.setArray(1, PostgresUtil.ListToArray(conn, codUnidades));
			stmt.setString(2, Servico.TIPO_CALIBRAGEM);
			stmt.setArray(3, PostgresUtil.ListToArray(conn, codUnidades));
			stmt.setString(4, Servico.TIPO_INSPECAO);
			stmt.setArray(5, PostgresUtil.ListToArray(conn, codUnidades));
			stmt.setString(6, Servico.TIPO_MOVIMENTACAO);
			stmt.setArray(7, PostgresUtil.ListToArray(conn, codUnidades));
			stmt.setString(8, Servico.TIPO_CALIBRAGEM);
			stmt.setArray(9, PostgresUtil.ListToArray(conn, codUnidades));
			stmt.setString(10, Servico.TIPO_INSPECAO);
			stmt.setArray(11, PostgresUtil.ListToArray(conn, codUnidades));
			stmt.setString(12, Servico.TIPO_MOVIMENTACAO);
			stmt.setDate(13, dataInicial);
			stmt.setDate(14, DateUtils.toSqlDate(DateUtils.getUltimoDiaMes(dataInicial)));
			rSet = stmt.executeQuery();
			int tempCalFechadas = 0;
			int tempInspFechadas = 0;
			int tempMovFechadas = 0;
			int tempCalAbertas = 0;
			int tempInspAbertas = 0;
			int tempMovAbertas = 0;
			while (rSet.next()) {
				resumoDia = new ResumoServicos();
				abertos = new ResumoServicos.Servicos();
				fechados = new ResumoServicos.Servicos();
				fechadosAc = new ResumoServicos.Servicos();
				abertosAc = new ResumoServicos.Servicos();
				resumoDia.setDia(rSet.getDate("DATA").getDate());
				// quantidade de serviços abertos no dia
				abertos.calibragem = rSet.getInt("CAL_ABERTAS");
				abertos.inspecao = rSet.getInt("INSP_ABERTAS");
				abertos.movimentacao = rSet.getInt("MOV_ABERTAS");
				// quantidade de serviços fechados no dia
				fechados.calibragem = rSet.getInt("CAL_FECHADAS");
				fechados.inspecao = rSet.getInt("INSP_FECHADAS");
				fechados.movimentacao = rSet.getInt("MOV_FECHADAS");
				// contador do acumulado, conta quantos serviços no total ja foram fechados
				tempCalFechadas += rSet.getInt("CAL_FECHADAS");
				tempInspFechadas += rSet.getInt("INSP_FECHADAS");
				tempMovFechadas += rSet.getInt("MOV_FECHADAS");
				//contador do acumulado, conta quantos serviços estão em aberto no dia
				// leva em consideração o total aberto - o arrumado no dia
				tempCalAbertas = tempCalAbertas + abertos.calibragem - fechados.calibragem;
				tempInspAbertas = tempInspAbertas + abertos.inspecao - fechados.inspecao;
				tempMovAbertas = tempMovAbertas + abertos.movimentacao - fechados.movimentacao;
				// associa os valores com os temporários
				fechadosAc.calibragem = tempCalFechadas;
				fechadosAc.inspecao = tempInspFechadas;
				fechadosAc.movimentacao = tempMovFechadas;
				// associa os valores com os temporários
				abertosAc.calibragem = tempCalAbertas;
				abertosAc.inspecao = tempInspAbertas;
				abertosAc.movimentacao = tempMovAbertas;
				// seta os objetos internos do Resumo
				resumoDia.setAbertos(abertos);
				resumoDia.setFechados(fechados);
				resumoDia.setAcumuladoFechados(fechadosAc);
				resumoDia.setAcumuladoAbertos(abertosAc);
				servicos.add(resumoDia);
			}

		}finally{
			closeConnection(conn, stmt, rSet);
		}
		L.d(TAG, servicos.toString());
		return servicos;
	}

	private ResultSet getPrevisaoCompra(long codUnidade, long dataInicial, Long dataFinal) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT\n" +
                    "    EXTRATO.\"Cod Pneu\",\n" +
                    "    EXTRATO.\"Marca\",\n" +
                    "    EXTRATO.\"Modelo\",\n" +
                    "    EXTRATO.\"Medidas\",\n" +
                    "    EXTRATO.\"Qtd de aferições\",\n" +
                    "    EXTRATO.\"Dta 1a aferição\",\n" +
                    "    EXTRATO.\"Dta última aferição\",\n" +
                    "    EXTRATO.\"Dias ativo\",\n" +
                    "    EXTRATO.\"Média de KM por dia\",\n" +
                    "    EXTRATO.\"Maior medição vida\",\n" +
                    "    EXTRATO.\"Menor sulco atual\",\n" +
                    "    EXTRATO.\"Milimetros gastos\",\n" +
                    "    EXTRATO.\"Kms por milimetro\",\n" +
                    "    EXTRATO.\"Kms a percorrer\",\n" +
                    "    EXTRATO.\"Dias restantes\",\n" +
                    "    to_char(EXTRATO.\"Previsão de troca\", 'DD/MM/YYYY') as \"Previsão de troca\"\n" +
                    "FROM\n" +
                    "    -- Dentro desse select uso as variáveis criadas no select interno para fazer as contas e formatação dos valores\n" +
                    "    (SELECT P.CODIGO AS \"Cod Pneu\",\n" +
                    "        MAP.NOME as \"Marca\",\n" +
                    "        MP.nome as \"Modelo\",\n" +
                    "        DP.largura || '/' || DP.altura || ' R'|| DP.aro as \"Medidas\",\n" +
                    "        DADOS.QT_AFERICOES AS \"Qtd de aferições\",\n" +
                    "        to_char(DADOS.PRIMEIRA_AFERICAO, 'DD/MM/YYYY') AS \"Dta 1a aferição\",\n" +
                    "        to_char(DADOS.ULTIMA_AFERICAO, 'DD/MM/YYYY') AS \"Dta última aferição\",\n" +
                    "        DADOS.TOTAL_DIAS AS \"Dias ativo\",\n" +
                    "        round(CASE WHEN DADOS.TOTAL_DIAS > 0 THEN\n" +
                    "          DADOS.TOTAL_KM / DADOS.TOTAL_DIAS END)  AS \"Média de KM por dia\",\n" +
                    "        round(DADOS.MAIOR_SULCO::DECIMAL, 2) AS \"Maior medição vida\",\n" +
                    "        round(DADOS.MENOR_SULCO::DECIMAL, 2) AS \"Menor sulco atual\",\n" +
                    "        round(DADOS.SULCO_GASTO::DECIMAL, 2) AS \"Milimetros gastos\",\n" +
                    "        round(DADOS.KM_POR_MM::DECIMAL, 2)  AS \"Kms por milimetro\",\n" +
                    "        round(((DADOS.KM_POR_MM) * DADOS.SULCO_RESTANTE)::DECIMAL) AS \"Kms a percorrer\",\n" +
                    "        TRUNC(CASE WHEN DADOS.TOTAL_KM > 0 AND DADOS.TOTAL_DIAS > 0  AND (DADOS.TOTAL_KM / DADOS.TOTAL_DIAS) > 0 THEN\n" +
                    "        ((DADOS.KM_POR_MM) * DADOS.SULCO_RESTANTE)/(DADOS.TOTAL_KM / DADOS.TOTAL_DIAS)\n" +
                    "          ELSE 0 END) AS \"Dias restantes\",\n" +
                    "        CASE WHEN DADOS.TOTAL_KM > 0 AND DADOS.TOTAL_DIAS > 0 AND (DADOS.TOTAL_KM / DADOS.TOTAL_DIAS) > 0 THEN\n" +
                    "        (((DADOS.KM_POR_MM) * DADOS.SULCO_RESTANTE)/(DADOS.TOTAL_KM / DADOS.TOTAL_DIAS))::INTEGER + CURRENT_DATE::DATE\n" +
                    "         END AS \"Previsão de troca\"\n" +
                    "  FROM PNEU P JOIN\n" +
                    "        (SELECT\n" +
                    "          -- Dentro desse select são criadas as \"variaveis\" para realizar os calculos, tornando mais facil a leitura\n" +
                    "        AV.cod_pneu AS COD_PNEU, AV.COD_UNIDADE AS COD_UNIDADE,\n" +
                    "        -- quantidade de afericoes que esse pneu ja teve\n" +
                    "        COUNT(AV.altura_sulco_central) AS QT_AFERICOES,\n" +
                    "        -- data da primeira afericao\n" +
                    "        MIN(A.data_hora)::DATE AS PRIMEIRA_AFERICAO,\n" +
                    "        -- data da ultima afericao\n" +
                    "        MAX(A.data_hora)::DATE AS ULTIMA_AFERICAO,\n" +
                    "        -- dias entre a primeira e a ultima afericao, ou total de dias rodando\n" +
                    "        (MAX(A.data_hora)::DATE - MIN(A.data_hora)::DATE) AS TOTAL_DIAS,\n" +
                    "        -- total de KM rodado (ja somando separadamente cada placa que o pneu passou)\n" +
                    "        MAX(TOTAL_KM.TOTAL_KM) AS TOTAL_KM,\n" +
                    "        -- maior altura de sulco de todas as aferições\n" +
                    "        MAX(GREATEST(AV.altura_sulco_central, AV.altura_sulco_externo, AV.altura_sulco_interno)) AS MAIOR_SULCO,\n" +
                    "        -- menor altura de sulco de todas as aferições\n" +
                    "        MIN(LEAST(AV.altura_sulco_central, AV.altura_sulco_externo, AV.altura_sulco_interno)) AS MENOR_SULCO,\n" +
                    "        -- sulco gasto (diferença entre a maior e a menor medição\n" +
                    "        MAX(GREATEST(AV.altura_sulco_central, AV.altura_sulco_externo, AV.altura_sulco_interno)) -\n" +
                    "        MIN(LEAST(AV.altura_sulco_central, AV.altura_sulco_externo, AV.altura_sulco_interno)) AS SULCO_GASTO,\n" +
                    "        -- case para calcular a quantidade de borracha restante usando as vidas como parametro\n" +
                    "        CASE WHEN (CASE WHEN P.vida_atual = P.vida_total THEN MIN(LEAST(AV.altura_sulco_central, AV.altura_sulco_externo, AV.altura_sulco_interno)) - ERP.sulco_minimo_descarte\n" +
                    "                    WHEN P.VIDA_ATUAL < P.VIDA_TOTAL THEN MIN(LEAST(AV.altura_sulco_central, AV.altura_sulco_externo, AV.altura_sulco_interno)) - ERP.sulco_minimo_RECAPAGEM END) < 0\n" +
                    "          THEN 0\n" +
                    "          ELSE (CASE WHEN P.vida_atual = P.vida_total THEN MIN(LEAST(AV.altura_sulco_central, AV.altura_sulco_externo, AV.altura_sulco_interno)) - ERP.sulco_minimo_descarte\n" +
                    "                  WHEN P.VIDA_ATUAL < P.VIDA_TOTAL THEN MIN(LEAST(AV.altura_sulco_central, AV.altura_sulco_externo, AV.altura_sulco_interno)) - ERP.sulco_minimo_RECAPAGEM\n" +
                    "                  END)\n" +
                    "        END AS SULCO_RESTANTE,\n" +
                    "        -- km por milimetro\n" +
                    "        CASE WHEN (MAX(A.data_hora)::DATE - MIN(A.data_hora)::DATE)::INT > 0 THEN\n" +
                    "                MAX(TOTAL_KM.TOTAL_KM) / MAX(GREATEST(AV.altura_sulco_central, AV.altura_sulco_externo, AV.altura_sulco_interno)) -\n" +
                    "                MIN(LEAST(AV.altura_sulco_central, AV.altura_sulco_externo, AV.altura_sulco_interno))\n" +
                    "            ELSE 0\n" +
                    "        END as KM_POR_MM\n" +
                    "        FROM AFERICAO_VALORES AV JOIN AFERICAO A ON A.CODIGO = AV.cod_afericao\n" +
                    "          JOIN PNEU P ON P.CODIGO = AV.COD_PNEU AND P.COD_UNIDADE = AV.COD_UNIDADE\n" +
                    "          JOIN empresa_restricao_pneu ERP ON ERP.COD_UNIDADE = AV.COD_UNIDADE\n" +
                    "          JOIN\n" +
                    "             -- total de km rodado ponderando as placas que o pneu passou\n" +
                    "             (SELECT TOTAL_KM_RODADO.COD_PNEU AS COD_PNEU, TOTAL_KM_RODADO.COD_UNIDADE AS COD_UNIDADE,\n" +
                    "                      SUM(TOTAL_KM_RODADO.KM_RODADO) AS TOTAL_KM\n" +
                    "                FROM (SELECT AV.cod_pneu AS COD_PNEU, AV.cod_unidade AS COD_UNIDADE, A.placa_veiculo,\n" +
                    "                  MAX(A.KM_VEICULO) - MIN(A.KM_VEICULO) AS KM_RODADO\n" +
                    "                  FROM AFERICAO_VALORES AV JOIN AFERICAO A ON A.CODIGO = AV.COD_AFERICAO\n" +
                    "                  GROUP BY 1,2,3) AS TOTAL_KM_RODADO\n" +
                    "            GROUP BY 1,2) AS TOTAL_KM ON TOTAL_KM.COD_PNEU = AV.COD_PNEU AND TOTAL_KM.COD_UNIDADE = AV.COD_UNIDADE\n" +
                    "        GROUP BY 1,2, P.VIDA_ATUAL, P.VIDA_TOTAL,ERP.sulco_minimo_descarte, ERP.sulco_minimo_RECAPAGEM)\n" +
                    "  AS DADOS ON DADOS.COD_PNEU = P.CODIGO AND DADOS.COD_UNIDADE = P.COD_UNIDADE\n" +
                    "  JOIN dimensao_pneu DP ON DP.CODIGO = P.cod_dimensao\n" +
                    "  JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE\n" +
                    "  JOIN modelo_pneu MP ON MP.CODIGO = P.COD_MODELO AND MP.COD_EMPRESA = U.cod_empresa\n" +
                    "  JOIN marca_pneu MAP ON MAP.codigo = MP.cod_marca\n" +
                    "  WHERE P.cod_unidade = ?) AS EXTRATO\n" +
                    "WHERE EXTRATO.\"Previsão de troca\" BETWEEN ? AND ? " +
                    "ORDER BY \"Previsão de troca\" ASC;");
			stmt.setLong(1, codUnidade);
            stmt.setDate(2, DateUtils.toSqlDate(new Date(dataInicial)));
            stmt.setDate(3, DateUtils.toSqlDate(new Date(dataFinal)));
			rSet = stmt.executeQuery();
			return rSet;
		}finally {
			closeConnection(conn, null, null);
		}
	}

	public void getPrevisaoCompraCsv(Long codUnidade, long dataInicial, long dataFinal, OutputStream outputStream) throws IOException, SQLException{
		new CsvWriter().write(getPrevisaoCompra(codUnidade, dataInicial, dataFinal), outputStream);
	}

	public Report getPrevisaoCompraReport(Long codUnidade, long dataInicial, long dataFinal) throws SQLException{
		return ReportConverter.createReport(getPrevisaoCompra(codUnidade, dataInicial, dataFinal));
	}

	private List<Faixa> populaFaixas(List<Faixa> faixas, List<Integer> valores){
		Collections.sort(valores);
		int integer = 0;
		// percorre todas as faixas
		for (Faixa faixa: faixas) {
			// percorre todos os valores
			for (int i = 0; i<valores.size(); i++) {
				integer = valores.get(i);
				// se a faixa começa com 0, veirica se é >= inicio e <= fim
				if (faixa.getInicio() == 0) {
					if (integer >= faixa.getInicio() && integer <= faixa.getFim()) {
						faixa.setTotalPneus(faixa.getTotalPneus() + 1);
						valores.remove(i);
						i--;
					}
				}
				// se a faixa for do lado negativo, a comparação se da de forma diferente >= inicio <fim
				else if (faixa.getInicio() < 0) {
					// verifica se o valor esta apto a entrar na faixa
					if (integer >= faixa.getInicio() && integer < faixa.getFim()) {
						faixa.setTotalPneus(faixa.getTotalPneus() + 1);
						valores.remove(i);
						i--;
					}
					// > inicio <= fim
				}else if(integer > faixa.getInicio() && integer <= faixa.getFim()){
					faixa.setTotalPneus(faixa.getTotalPneus() + 1);
					valores.remove(i);
					i--;
				}
			}
		}
		L.d(TAG, "Populadas: " + faixas.toString());
		return faixas;
	}

	private List<Faixa> criaFaixas(int base, int escala){
		List<Faixa> faixas = new ArrayList<>();
		// cria a primeira faixa de 0 até a restrição imposta pela empresa (3% por exemplo)
		Faixa faixa = new Faixa();
		faixa.setInicio(0);
		faixa.setFim(base);
		faixas.add(faixa);
		// cria a primeira faixas negativa, que vai de -3 a 0
		faixa = new Faixa();
		faixa.setInicio(base*-1);
		faixa.setFim(0);
		faixas.add(faixa);

		int inicio = base;
		int fim = base;

		// 1- verificar o próximo multiplo de 10 a partir da base(restricao)
		while (fim % 10 != 0) {
			fim++;
		}
		// cria a segunda faixa positiva, que vai de 3 a 10(calculado com o while acima)
		faixa = new Faixa();
		faixa.setInicio(inicio);
		faixa.setFim(fim);
		faixas.add(faixa);
		// cria a segunda faixa negativa, que vai de -10 a -3
		faixa = new Faixa();
		faixa.setInicio(fim*-1);
		faixa.setFim(inicio*-1);
		faixas.add(faixa);

		while (fim < 100) {
			inicio = fim;
			fim = inicio + escala;
			faixa = new Faixa();
			faixa.setInicio(inicio);
			faixa.setFim(fim);
			faixas.add(faixa);

			faixa = new Faixa();
			faixa.setInicio(fim * -1);
			faixa.setFim(inicio * -1);
			faixas.add(faixa);
		}

		Collections.sort(faixas, new CustomComparatorFaixas());
		return faixas;
	}

	private List<Faixa> getFaixas(List<Double> valores){
		Double minimo = (double) 0;
		Double cota = (valores.get(0) / 5)+ 1;
		Double maximo = cota;
		int totalPneus = valores.size();
		List<Faixa> faixas = new ArrayList<>();
		//cria as faixas
		L.d("kk", valores.toString());
		while(minimo < valores.get(0)){
			Faixa faixa = new Faixa();
			faixa.setInicio(minimo);
			faixa.setFim(maximo);
			minimo = maximo;
			maximo = maximo + cota;
			faixas.add(faixa);
		}
		//soma cada sulco para a sua devida faixa
		for(Faixa faixa : faixas){
			for (int i = 0; i < valores.size(); i++) {
				if(valores.get(i)>= faixa.getInicio() && valores.get(i) < faixa.getFim()){
					faixa.setTotalPneus(faixa.getTotalPneus()+1);;
					valores.remove(i);
					i--;
				}
			}
			faixa.setPorcentagem((double)faixa.getTotalPneus()/totalPneus);
		}
		// cria a faixa de itens não aferidos, com o que sobrou da lista valores
		Faixa faixa = new Faixa();
		faixa.setNaoAferidos(true);
		faixa.setTotalPneus(valores.size());
		faixa.setPorcentagem((double) valores.size() / totalPneus);
		faixas.add(faixa);

		return faixas;
	}

	private Aderencia createAderencia(double meta, int dia){
		Aderencia aderencia = new Aderencia();
		aderencia.setDia(dia);
		aderencia.setMeta(meta);
		return aderencia;
	}

	private void setPorcentagemFaixas(List<Faixa> faixas, int total){
		L.d(TAG, String.valueOf(total));
		for (Faixa faixa : faixas) {
			if (faixa.getTotalPneus() == 0) {
				faixa.setPorcentagem(0);
			}else{
				double porcentagem = (double) faixa.getTotalPneus() / total;
				faixa.setPorcentagem(porcentagem);
			}
		}
	}


	//ordena as faixas pelo inicio de cada uma
	private class CustomComparatorFaixas implements Comparator<Faixa>{
		/**
		 * Compara primeiro pela pontuação e depois pela devolução em NF, evitando empates
		 */
		@Override
		public int compare(Faixa o1, Faixa o2) {
			Integer valor1 = Double.compare(o1.getInicio(), o2.getInicio());
			return valor1;
		}			
	}
}
