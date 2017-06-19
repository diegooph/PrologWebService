package br.com.zalf.prolog.webservice.frota.pneu.relatorios;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.commons.CsvWriter;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.report.ReportTransformer;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import br.com.zalf.prolog.webservice.commons.util.L;
import br.com.zalf.prolog.webservice.commons.util.PostgresUtil;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.AfericaoDaoImpl;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDaoImpl;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.Aderencia;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.Faixa;
import br.com.zalf.prolog.webservice.frota.pneu.relatorios.model.ResumoServicos;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.Servico;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDaoImpl;

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

	private static final String PNEUS_RESUMO_SULCOS="SELECT COALESCE(ALTURA_SULCO_CENTRAL_INTERNO, ALTURA_SULCO_CENTRAL_INTERNO, -1) AS ALTURA_SULCO_CENTRAL FROM PNEU WHERE "
			+ "COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND STATUS LIKE ANY (ARRAY[?])  ORDER BY 1 DESC";

	private static final String SULCOS_PNEUS_BY_FAIXAS = "SELECT MP.NOME AS MARCA, MP.CODIGO AS COD_MARCA, P.CODIGO, P.PRESSAO_ATUAL, P.VIDA_ATUAL, "
			+ "P.VIDA_TOTAL, MOP.NOME AS MODELO, MOP.CODIGO AS COD_MODELO, MOP.QT_SULCOS AS QT_SULCOS_MODELO, PD.CODIGO AS COD_DIMENSAO, PD.ALTURA, PD.LARGURA, PD.ARO, P.PRESSAO_RECOMENDADA, "
			+ "P.altura_sulcos_novos,P.altura_sulco_CENTRAL_INTERNO, P.altura_sulco_CENTRAL_EXTERNO, P.altura_sulco_INTERNO, P.altura_sulco_EXTERNO, p.status, "
			+ "MB.codigo AS COD_MODELO_BANDA, MB.nome AS NOME_MODELO_BANDA, MAB.codigo AS COD_MARCA_BANDA, MAB.nome AS NOME_MARCA_BANDA, MAB.QT_SULCOS AS QT_SULCOS_BANDA\n "
			+ "FROM PNEU P "
			+ "JOIN MODELO_PNEU MOP ON MOP.CODIGO = P.COD_MODELO "
			+ "JOIN MARCA_PNEU MP ON MP.CODIGO = MOP.COD_MARCA "
			+ "JOIN DIMENSAO_PNEU PD ON PD.CODIGO = P.COD_DIMENSAO "
			+ "JOIN UNIDADE U ON U.CODIGO = P.COD_UNIDADE "
			+ "JOIN EMPRESA E ON E.CODIGO = U.COD_EMPRESA "
			+ "LEFT JOIN modelo_banda MB ON MB.codigo = P.cod_modelo_banda AND MB.cod_empresa = U.cod_empresa\n "
			+ "LEFT JOIN marca_banda MAB ON MAB.codigo = MB.cod_marca AND MAB.cod_empresa = MB.cod_empresa\n "
			+ "WHERE P.ALTURA_SULCO_CENTRAL >= ? AND P.ALTURA_SULCO_CENTRAL < ? AND E.CODIGO = ? AND P.COD_UNIDADE::TEXT LIKE ? "
			+ "ORDER BY P.ALTURA_SULCO_CENTRAL_INTERNO DESC "
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
	public List<Pneu> getPneusByFaixaSulco(double inicioFaixa, double fimFaixa, Long codEmpresa, String codUnidade, long limit, long offset) throws SQLException {
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
		populaFaixas(faixas, valores);
		setPorcentagemFaixas(faixas, totalValores);
		Faixa faixa = new Faixa();
		faixa.setNaoAferidos(true);
		faixa.setTotalPneus(naoAferidos);
		faixa.setPorcentagem((double) naoAferidos/ (double) totalValores);
		faixas.add(faixa);
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

	@Override
	public void getPrevisaoTrocaCsv(Long codUnidade, long dataInicial, long dataFinal, OutputStream outputStream) throws IOException, SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = getPrevisaoTrocaStatement(conn, codUnidade, dataInicial, dataFinal);
			rSet = stmt.executeQuery();
			new CsvWriter().write(rSet, outputStream);
		} finally {
			closeConnection(conn, stmt, rSet);
		}
	}

	@Override
	public Report getPrevisaoTrocaReport(Long codUnidade, long dataInicial, long dataFinal) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = getPrevisaoTrocaStatement(conn, codUnidade, dataInicial, dataFinal);
			rSet = stmt.executeQuery();
			return ReportTransformer.createReport(rSet);
		} finally {
			closeConnection(conn, stmt, rSet);
		}
	}

	@Override
	public void getPrevisaoTrocaConsolidadoCsv(Long codUnidade, long dataInicial, long dataFinal, OutputStream outputStream)
			throws IOException, SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = getPrevisaoTrocaConsolidadoStatement(conn, codUnidade, dataInicial, dataFinal);
			rSet = stmt.executeQuery();
			new CsvWriter().write(rSet, outputStream);
		} finally {
			closeConnection(conn, stmt, rSet);
		}
	}

	@Override
	public Report getPrevisaoTrocaConsolidadoReport(Long codUnidade, long dataInicial, long dataFinal) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = getPrevisaoTrocaConsolidadoStatement(conn, codUnidade, dataInicial, dataFinal);
			rSet = stmt.executeQuery();
			return ReportTransformer.createReport(rSet);
		} finally {
			closeConnection(conn, stmt, rSet);
		}
	}

	private PreparedStatement getPrevisaoTrocaStatement(Connection conn, long codUnidade, long dataInicial, Long dataFinal)
			throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("  SELECT\n" +
				"    VAP.\"COD PNEU\",\n" +
				"    VAP.\"MARCA\",\n" +
				"    VAP.\"MODELO\",\n" +
				"    VAP.\"MEDIDAS\",\n" +
				"    VAP.\"QTD DE AFERIÇÕES\",\n" +
				"    VAP.\"DTA 1a AFERIÇÃO\",\n" +
				"    VAP.\"DTA ÚLTIMA AFERIÇÃO\",\n" +
				"    VAP.\"DIAS ATIVO\",\n" +
				"    VAP.\"MÉDIA KM POR DIA\",\n" +
				"    VAP.\"MAIOR MEDIÇÃO VIDA\",\n" +
				"    VAP.\"MENOR SULCO ATUAL\",\n" +
				"    VAP.\"MILIMETROS GASTOS\",\n" +
				"    VAP.\"KMS POR MILIMETRO\",\n" +
				"    VAP.\"KMS A PERCORRER\",\n" +
				"    VAP.\"DIAS RESTANTES\",\n" +
				"    to_char(VAP.\"PREVISÃO DE TROCA\", 'DD/MM/YYYY') as \"PREVISÃO DE TROCA\"\n" +
				"FROM\n" +
				"    VIEW_ANALISE_PNEUS VAP\n" +
				"WHERE VAP.cod_unidade = ? AND VAP.\"PREVISÃO DE TROCA\" BETWEEN ? AND ? AND VAP.\"STATUS PNEU\" = ? \n" +
				"  ORDER BY VAP.\"PREVISÃO DE TROCA\" ASC");
		stmt.setLong(1, codUnidade);
		stmt.setDate(2, DateUtils.toSqlDate(new Date(dataInicial)));
		stmt.setDate(3, DateUtils.toSqlDate(new Date(dataFinal)));
		stmt.setString(4, Pneu.EM_USO);
		return stmt;
	}

	private PreparedStatement getPrevisaoTrocaConsolidadoStatement(Connection conn, long codUnidade, long dataInicial, Long dataFinal)
			throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT\n" +
				"  to_char(VAP.\"PREVISÃO DE TROCA\", 'DD/MM/YYYY') AS \"DATA\",\n" +
				"  VAP.\"MARCA\",\n" +
				"  VAP.\"MODELO\",\n" +
				"  VAP.\"MEDIDAS\",\n" +
				"  COUNT(VAP.\"MODELO\") as \"QUANTIDADE\"\n" +
				"FROM\n" +
				"    -- Dentro dessa view uso as variáveis criadas no select interno para fazer as contas e formatação dos valores\n" +
				"    VIEW_ANALISE_PNEUS VAP\n" +
				"WHERE VAP.cod_unidade = ? and VAP.\"PREVISÃO DE TROCA\" BETWEEN ? AND ? AND VAP.\"STATUS PNEU\" = ?\n" +
				"GROUP BY VAP.\"PREVISÃO DE TROCA\", VAP.\"MARCA\",  VAP.\"MODELO\",  VAP.\"MEDIDAS\"\n" +
				"ORDER BY VAP.\"PREVISÃO DE TROCA\" ASC, 5 DESC;");
		stmt.setLong(1, codUnidade);
		stmt.setDate(2, DateUtils.toSqlDate(new Date(dataInicial)));
		stmt.setDate(3, DateUtils.toSqlDate(new Date(dataFinal)));
        stmt.setString(4, Pneu.EM_USO);
		return  stmt;
	}

	@Override
	public void getAderenciaPlacasCsv(Long codUnidade, long dataInicial, long dataFinal, OutputStream outputStream)
			throws IOException, SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = getAderenciaPlacasStatement(conn, codUnidade, dataInicial, dataFinal);
			rSet = stmt.executeQuery();
			new CsvWriter().write(rSet, outputStream);
		} finally {
			closeConnection(conn, stmt, rSet);
		}
	}

	@Override
	public Report getAderenciaPlacasReport(Long codUnidade, long dataInicial, long dataFinal) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = getAderenciaPlacasStatement(conn, codUnidade, dataInicial, dataFinal);
			rSet = stmt.executeQuery();
			return ReportTransformer.createReport(rSet);
		} finally {
			closeConnection(conn, stmt, rSet);
		}
	}

	private PreparedStatement getAderenciaPlacasStatement(Connection conn, long codUnidade, long dataInicial, Long dataFinal)
			throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT CALCULO.PLACA as \"PLACA\",\n" +
				"  COUNT(CALCULO.PLACA) AS \"QTD DE AFERIÇÕES\",\n" +
				"\n" +
				"  CASE WHEN\n" +
				"  MAX(CALCULO.DIAS_ENTRE_AFERICOES) IS NOT NULL THEN MAX(CALCULO.DIAS_ENTRE_AFERICOES)::TEXT ELSE 'NÃO AFERIDO' END AS \"MÁX DE DIAS ENTRE AFERIÇÕES\",\n" +
				"  CASE WHEN\n" +
				"  MIN(CALCULO.DIAS_ENTRE_AFERICOES) IS NOT NULL THEN MIN(CALCULO.DIAS_ENTRE_AFERICOES)::TEXT ELSE 'NÃO AFERIDO' END AS \"MIN DE DIAS ENTRE AFERIÇÕES\",\n" +
				"  CASE WHEN\n" +
				"    MAX(CALCULO.DIAS_ENTRE_AFERICOES) IS NOT NULL THEN trunc(CASE WHEN SUM(CALCULO.DIAS_ENTRE_AFERICOES) IS NOT NULL THEN\n" +
				"            SUM(CALCULO.DIAS_ENTRE_AFERICOES) /\n" +
				"            SUM(CASE WHEN CALCULO.DIAS_ENTRE_AFERICOES IS NOT NULL THEN 1 ELSE 0 END)\n" +
				"  END)::TEXT ELSE 'NÃO AFERIDO' END AS \"MÉDIA DIAS ENTRE ADERIÇÕES\",\n" +
				"  sum(CASE WHEN CALCULO.DIAS_ENTRE_AFERICOES <= CALCULO.PERIODO_AFERICAO THEN 1 ELSE 0 END) as \"QTD AFERIÇÕES DENTRO DA META\",\n" +
				"  TRUNC(sum(CASE WHEN CALCULO.DIAS_ENTRE_AFERICOES <= CALCULO.PERIODO_AFERICAO THEN 1 ELSE 0 END) / COUNT(CALCULO.PLACA)::NUMERIC * 100) || '%' AS \"ADERÊNCIA\"\n" +
				"  FROM\n" +
				"-- QUERY PARA CONTAR A QUANTIDADES DE AFERIÇÕES DENTRO DO PRAZO REALIZADAS, POR PLACA E RESPEITANDO UM PERÍODO SELECIONADO\n" +
				"(SELECT A.placa_veiculo AS PLACA,  A.data_hora, R.periodo_afericao AS PERIODO_AFERICAO,\n" +
				"            CASE WHEN A.placa_veiculo = lag(A.PLACA_VEICULO) over (ORDER BY placa_veiculo, data_hora) THEN\n" +
				"            EXTRACT( DAYS FROM  A.DATA_HORA - lag(A.data_hora) over (ORDER BY placa_veiculo, data_hora))\n" +
				"            END AS DIAS_ENTRE_AFERICOES\n" +
				"      FROM afericao A\n" +
				"        JOIN VEICULO V ON V.placa = A.placa_veiculo\n" +
				"        JOIN empresa_restricao_pneu R ON R.cod_unidade = V.cod_unidade\n" +
				"    WHERE v.cod_unidade = ? and A.data_hora BETWEEN ? AND ?\n" +
				"    ORDER BY 1,2) AS CALCULO\n" +
				"GROUP BY 1\n" +
				"ORDER BY 7 DESC, 3;");
		stmt.setLong(1, codUnidade);
		stmt.setDate(2, DateUtils.toSqlDate(new Date(dataInicial)));
		stmt.setDate(3, DateUtils.toSqlDate(new Date(dataFinal)));
		return stmt;
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
			return Double.compare(o1.getInicio(), o2.getInicio());
		}
	}

	@Override
	public void getDadosUltimaAfericaoCsv(Long codUnidade, OutputStream outputStream)
			throws IOException, SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = getDadosUltimaAfericaoStatement(conn, codUnidade);
			rSet = stmt.executeQuery();
			new CsvWriter().write(rSet, outputStream);
		} finally {
			closeConnection(conn, stmt, rSet);
		}
	}

	@Override
	public Report getDadosUltimaAfericaoReport(Long codUnidade) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try {
			conn = getConnection();
			stmt = getDadosUltimaAfericaoStatement(conn, codUnidade);
			rSet = stmt.executeQuery();
			return ReportTransformer.createReport(rSet);
		} finally {
			closeConnection(conn, stmt, rSet);
		}
	}

	private PreparedStatement getDadosUltimaAfericaoStatement(Connection conn, long codUnidade)
			throws SQLException {
		PreparedStatement stmt = conn.prepareStatement("SELECT\n" +
				"  P.codigo                                                                                   AS \"PNEU\",\n" +
				"  map.nome                                                                                   AS \"MARCA\",\n" +
				"  mp.nome                                                                                    AS \"MODELO\",\n" +
				"  ((((dp.largura || '/' :: TEXT) || dp.altura) || ' R' :: TEXT) || dp.aro)                   AS \"MEDIDAS\",\n" +
				"  coalesce(POSICAO_PNEU_VEICULO.PLACA_VEICULO_PNEU, '-')                                     AS \"PLACA\",\n" +
				"  coalesce(POSICAO_PNEU_VEICULO.VEICULO_TIPO, '-')                                           AS \"TIPO\",\n" +
				"  coalesce(POSICAO_PNEU_VEICULO.POSICAO_PNEU, '-')                                           AS \"POSIÇÃO\",\n" +
				"  coalesce(trunc(P.altura_sulco_interno :: NUMERIC, 2) :: TEXT, '-')                         AS \"SULCO INTERNO\",\n" +
				"  coalesce(trunc(P.altura_sulco_central_interno :: NUMERIC, 2) :: TEXT, '-')                         AS \"SULCO CENTRAL INTERNO\",\n" +
				"  coalesce(trunc(P.altura_sulco_central_externo :: NUMERIC, 2) :: TEXT, '-')                         AS \"SULCO CENTRAL EXTERNO\",\n" +
				"  coalesce(trunc(P.altura_sulco_externo :: NUMERIC, 2) :: TEXT, '-')                         AS \"SULCO EXTERNO\",\n" +
				"  coalesce(trunc(P.pressao_atual) :: TEXT, '-')                                              AS \"PRESSÃO (PSI)\",\n" +
				"  P.vida_atual                                                                               AS \"VIDA\",\n" +
				"  coalesce(to_char(DATA_ULTIMA_AFERICAO.ULTIMA_AFERICAO, 'DD/MM/YYYY HH:MM'), 'não aferido') AS \"ÚLTIMA AFERIÇÃO\"\n" +
				"FROM PNEU P\n" +
				"  JOIN dimensao_pneu dp ON dp.codigo = p.cod_dimensao\n" +
				"  JOIN unidade u ON u.codigo = p.cod_unidade\n" +
				"  JOIN modelo_pneu mp ON mp.codigo = p.cod_modelo AND mp.cod_empresa = u.cod_empresa\n" +
				"  JOIN marca_pneu map ON map.codigo = mp.cod_marca\n" +
				"  LEFT JOIN\n" +
				"  (SELECT\n" +
				"     PON.nomenclatura AS POSICAO_PNEU,\n" +
				"     VP.cod_pneu      AS CODIGO_PNEU,\n" +
				"     VP.placa         AS PLACA_VEICULO_PNEU,\n" +
				"     VP.cod_unidade   AS COD_UNIDADE_PNEU,\n" +
				"     VT.nome          AS VEICULO_TIPO\n" +
				"   FROM veiculo V\n" +
				"     JOIN veiculo_pneu VP ON VP.placa = V.placa AND VP.cod_unidade = V.cod_unidade\n" +
				"     JOIN veiculo_tipo vt ON v.cod_unidade = vt.cod_unidade AND v.cod_tipo = vt.codigo\n" +
				"     JOIN pneu_ordem_nomenclatura_unidade pon ON pon.cod_unidade = v.cod_unidade AND pon.cod_tipo_veiculo = v.cod_tipo\n" +
				"                                                 AND vp.posicao = pon.posicao_prolog\n" +
				"   WHERE V.cod_unidade = ?\n" +
				"   ORDER BY VP.cod_pneu) AS POSICAO_PNEU_VEICULO\n" +
				"    ON P.codigo = POSICAO_PNEU_VEICULO.CODIGO_PNEU AND P.cod_unidade = POSICAO_PNEU_VEICULO.COD_UNIDADE_PNEU\n" +
				"  LEFT JOIN\n" +
				"  (SELECT\n" +
				"     AV.cod_pneu,\n" +
				"     AV.cod_unidade   AS COD_UNIDADE_DATA,\n" +
				"     MAX(A.data_hora) AS ULTIMA_AFERICAO\n" +
				"   FROM AFERICAO A\n" +
				"     JOIN afericao_valores AV ON A.codigo = AV.cod_afericao\n" +
				"   GROUP BY 1, 2) AS DATA_ULTIMA_AFERICAO\n" +
				"    ON DATA_ULTIMA_AFERICAO.COD_UNIDADE_DATA = P.cod_unidade AND DATA_ULTIMA_AFERICAO.cod_pneu = P.codigo\n" +
				"WHERE P.cod_unidade = ?\n" +
				"ORDER BY \"PNEU\"");
		stmt.setLong(1, codUnidade);
		stmt.setLong(2, codUnidade);
		return stmt;
	}


}