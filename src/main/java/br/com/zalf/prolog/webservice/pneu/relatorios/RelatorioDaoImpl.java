package br.com.zalf.prolog.webservice.pneu.relatorios;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.zalf.prolog.models.pneu.Pneu;
import br.com.zalf.prolog.models.pneu.Restricao;
import br.com.zalf.prolog.models.pneu.relatorios.Aderencia;
import br.com.zalf.prolog.models.pneu.relatorios.Faixa;
import br.com.zalf.prolog.models.util.DateUtils;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDaoImpl;
import br.com.zalf.prolog.webservice.pneu.afericao.AfericaoDaoImpl;
import br.com.zalf.prolog.webservice.pneu.pneu.PneuDaoImpl;
import br.com.zalf.prolog.webservice.util.L;
import br.com.zalf.prolog.webservice.util.PostgresUtil;

/**
 * Classe responsável por estratificar os dados dos pneus.
 * @author jean
 *
 */
public class RelatorioDaoImpl extends DatabaseConnection{

	private static final String TAG = RelatorioDaoImpl.class.getSimpleName();

	private static final String PNEUS_RESUMO_SULCOS="SELECT COALESCE(ALTURA_SULCO_CENTRAL, ALTURA_SULCO_CENTRAL, 0) AS ALTURA_SULCO_CENTRAL FROM PNEU WHERE "
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


	public List<Faixa> getQtPneusByFaixaSulco(List<String> codUnidades, List<String> status)throws SQLException{

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
		return faixas;
	}

	/**
	 * Busca os pneus respeitando os filtros de limite de sulcos aplicados, além do codEmpresa e codUnidade
	 * @param inicioFaixa menor sulco a ser considerado
	 * @param fimFaixa maior sulco a ser considerado
	 * @param codEmpresa
	 * @param codUnidade
	 * @return
	 * @throws SQLException
	 */
	public List<Pneu> getPneusByFaixa(double inicioFaixa, double fimFaixa, Long codEmpresa, String codUnidade, long limit, long offset) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Pneu> pneus = new ArrayList<>();
		Pneu pneu = new Pneu();
		PneuDaoImpl pneuDaoImpl = new PneuDaoImpl();
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
				pneu = pneuDaoImpl.createPneu(rSet);
				pneus.add(pneu);
			}
		} finally {
			closeConnection(conn, stmt, rSet);
		}
		return pneus;
	}

	public void getResumoCalibragens(){};

	public List<Aderencia> getAderenciaByUnidade(int ano, int mes, Long codUnidade) throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;

		List<Aderencia> aderencias = new ArrayList<>();
		Aderencia aderencia = null;
		AfericaoDaoImpl afericaoDaoImpl = new AfericaoDaoImpl();
		VeiculoDaoImpl veiculoDaoImpl = new VeiculoDaoImpl();
		Restricao restricao = afericaoDaoImpl.getRestricoesByCodUnidade(codUnidade);

		Date dataAtual = new Date(System.currentTimeMillis());
		LocalDate dataInicial = LocalDate.of(ano, mes, 01);
		Date datainicial = Date.valueOf(dataInicial);

		double meta = 0;
		int totalVeiculos = 0;
		int ultimoDia = 0;
		int dia = 1;		

		if (dataAtual.getYear()+1900 == ano && dataAtual.getMonth()+1 == mes) {
			ultimoDia = dataAtual.getDate();
		}else{
			ultimoDia = DateUtils.getUltimoDiaMes(datainicial).getDate();
		}

		try{			
			conn = getConnection();
			totalVeiculos = veiculoDaoImpl.getTotalVeiculosByUnidade(codUnidade, conn);
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
					aderencias.add(createAderenciaVazia(meta, dia));	
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
						aderencias.add(createAderenciaVazia(meta, dia));	
						dia++;
					}
				}
			}
		}finally{
			closeConnection(conn, stmt, rSet);
		}
		return aderencias;
	}

	private Aderencia createAderenciaVazia(double meta, int dia){
		Aderencia aderencia = new Aderencia();
		aderencia.setDia(dia);
		aderencia.setMeta(meta);
		return aderencia;
	}

	public List<Faixa> getQtPneusByFaixaPressao(List<String> codUnidades, List<String> status)throws SQLException{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Faixa> faixas = null;
		AfericaoDaoImpl afericaoDaoImpl = new AfericaoDaoImpl();
		if (!codUnidades.get(0).equals("%")) {
			Restricao restricao = afericaoDaoImpl.getRestricoesByCodUnidade(Long.parseLong(codUnidades.get(0)));	
			Integer base = (int) Math.round(restricao.getToleranciaCalibragem()*100);
			faixas = criaFaixas(base, 30);
		}else{
			faixas = criaFaixas(0, 30);
		}		
		List<Integer> valores = new ArrayList<>();

		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT "
					+ "(((COALESCE(PRESSAO_ATUAL, PRESSAO_ATUAL, 0) - COALESCE(PRESSAO_RECOMENDADA, PRESSAO_RECOMENDADA, 0))/"
					+ "COALESCE(PRESSAO_RECOMENDADA, PRESSAO_RECOMENDADA, 0))*100)::INT AS PORC "
					+ "FROM PNEU "
					+ "WHERE COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND STATUS LIKE ANY (ARRAY[?])  "
					+ "ORDER BY 1 asc");
			stmt.setArray(1, PostgresUtil.ListToArray(conn, codUnidades));
			stmt.setArray(2, PostgresUtil.ListToArray(conn, status));
			rSet = stmt.executeQuery();
			while(rSet.next()){
				valores.add(rSet.getInt("PORC"));
			}
		}finally{
			closeConnection(conn, stmt, rSet);		
		}
		int totalValores = valores.size();
		populaFaixas(faixas, valores);
		setPorcentagemFaixas(faixas, totalValores);
		L.d(TAG, "Finalizado: " + faixas.toString());
		return faixas;
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

	public List<Faixa> criaFaixas(int base, int escala){
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

	public List<Faixa> populaFaixas(List<Faixa> faixas, List<Integer> valores){
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
