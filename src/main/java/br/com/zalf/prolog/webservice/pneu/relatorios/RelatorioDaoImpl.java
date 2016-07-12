package br.com.zalf.prolog.webservice.pneu.relatorios;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
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
			faixa.setPorcentagem(faixa.getTotalPneus()/totalPneus);
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
		L.d(TAG, codUnidades.toString());
		L.d(TAG, status.toString());
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		List<Faixa> faixas = new ArrayList<>();
		Faixa ma20 = new Faixa();
		ma20.setInicio(20);
		ma20.setFim(100);
		Faixa ma10 = new Faixa();
		ma10.setInicio(10);
		ma10.setFim(20);
		Faixa ma5 = new Faixa();
		ma5.setInicio(5);
		ma5.setFim(10);
		Faixa ok = new Faixa();
		ok.setInicio(-5);
		ok.setFim(5);
		Faixa me20 = new Faixa();
		me20.setInicio(-20);
		me20.setFim(-100);
		Faixa me10 = new Faixa();
		me10.setInicio(-10);
		me10.setFim(-20);
		Faixa me5 = new Faixa();
		me5.setInicio(-5);
		me5.setFim(-10);

		try{
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT COALESCE(PRESSAO_ATUAL, PRESSAO_ATUAL, 0) AS PRESSAO_ATUAL, " 
					+ "COALESCE(PRESSAO_RECOMENDADA, PRESSAO_RECOMENDADA, 0) AS PRESSAO_RECOMENDADA, " 
					+ "COALESCE(((pressao_atual/pressao_recomendada)-1)*100, ((pressao_atual/pressao_recomendada)-1)*100, 0)::INT AS PORC FROM PNEU WHERE " 
					+ "COD_UNIDADE::TEXT LIKE ANY (ARRAY[?]) AND STATUS LIKE ANY (ARRAY[?])  ORDER BY 1 DESC ");
			stmt.setArray(1, PostgresUtil.ListToArray(conn, codUnidades));
			stmt.setArray(2, PostgresUtil.ListToArray(conn, status));
			rSet = stmt.executeQuery();
			while(rSet.next()){
				L.d(TAG, String.valueOf(rSet.getDouble("PRESSAO_RECOMENDADA")));
				
				int valor = rSet.getInt("PORC"); 
				//L.d(TAG, String.valueOf(valor));
				
				if (valor >= ma20.getInicio() && valor < ma20.getFim()) {
					ma20.setTotalPneus(ma20.getTotalPneus()+1);
				}else if (valor >= ma10.getInicio() && valor < ma10.getFim()) {
					ma10.setTotalPneus(ma10.getTotalPneus()+1);
				}else if (valor >= ma5.getInicio() && valor < ma5.getFim()) {
					ma5.setTotalPneus(ma5.getTotalPneus()+1);
				}else if (valor >= ok.getInicio() && valor < ok.getFim()) {
					ok.setTotalPneus(ok.getTotalPneus()+1);
				}else if (valor >= me20.getInicio() && valor < me20.getFim()) {
					me20.setTotalPneus(me20.getTotalPneus()+1);
				}else if (valor >= me10.getInicio() && valor < me10.getFim()) {
					me10.setTotalPneus(me10.getTotalPneus()+1);
				}else if (valor >= me5.getInicio() && valor < me5.getFim()) {
					me5.setTotalPneus(me5.getTotalPneus()+1);
				}				
			}
			faixas.add(ma20);
			faixas.add(ma10);
			faixas.add(ma5);
			faixas.add(ok);
			faixas.add(me5);
			faixas.add(me10);
			faixas.add(me20);
		}finally{
			closeConnection(conn, stmt, rSet);		
		}
		return faixas;
	}

}
