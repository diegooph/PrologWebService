package br.com.zalf.prolog.webservice.pneu.relatorios;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.pneu.Pneu;
import br.com.zalf.prolog.models.pneu.relatorios.Faixa;
import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.pneu.pneu.PneuDaoImpl;
import br.com.zalf.prolog.webservice.util.L;
import br.com.zalf.prolog.webservice.util.PostgresUtil;

/**
 * Classe responsável por estratificar os dados dos pneus.
 * @author jean
 *
 */
public class RelatorioDaoImpl extends DatabaseConnection{

	private static final String TAG = "RelatorioPneus";

	private static final String PNEUS_RESUMO_SULCOS="SELECT ALTURA_SULCO_CENTRAL FROM PNEU WHERE COD_UNIDADE::TEXT LIKE ? AND STATUS "
			+ "LIKE ANY (ARRAY[?])  ORDER BY 1 DESC";

	private static final String PNEUS_BY_FAIXAS = "SELECT MP.NOME AS MARCA, MP.CODIGO AS COD_MARCA, P.CODIGO, P.PRESSAO_ATUAL, P.VIDA_ATUAL, "
			+ "P.VIDA_TOTAL, MOP.NOME AS MODELO, MOP.CODIGO AS COD_MODELO, PD.ALTURA, PD.LARGURA, PD.ARO, P.PRESSAO_RECOMENDADA, "
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


	public List<Faixa> getQtPneusByFaixaSulco(String codUnidade, List<String> status)throws SQLException{

		List<Double> valores = new ArrayList<>();

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
				
		try{
			conn = getConnection();
			stmt = conn.prepareStatement(PNEUS_RESUMO_SULCOS);
			stmt.setString(1, codUnidade);
			stmt.setArray(2, PostgresUtil.ListToArray(conn, status));
			//stmt.setArray(2, conn.createArrayOf("text", array));
			System.out.println(stmt.toString());
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
		Double cota = valores.get(0) / 5;
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
				if(valores.get(i)> faixa.getInicio() && valores.get(i) <= faixa.getFim()){
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
			stmt = conn.prepareStatement(PNEUS_BY_FAIXAS);
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

}
