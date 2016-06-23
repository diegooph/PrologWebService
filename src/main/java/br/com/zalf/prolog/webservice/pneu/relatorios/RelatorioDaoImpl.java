package br.com.zalf.prolog.webservice.pneu.relatorios;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.zalf.prolog.models.pneu.relatorios.ResumoSulcos;
import br.com.zalf.prolog.webservice.DatabaseConnection;

/**
 * Classe responsável por estratificar os dados dos pneus.
 * @author jean
 *
 */
public class RelatorioDaoImpl extends DatabaseConnection{

	private static final String TAG = "Relatorio Pneus";

	private static final String PNEUS_RESUMO_SULCOS="SELECT ALTURA_SULCO_CENTRAL FROM PNEU WHERE COD_UNIDADE =? ORDER BY 1 DESC";


	public ResumoSulcos getResumoSulcos(Long codUnidade, String status)throws SQLException{

		ResumoSulcos resumo = new ResumoSulcos();
		List<Double> valores = new ArrayList<>();

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		try{
			conn = getConnection();
			stmt = conn.prepareStatement(PNEUS_RESUMO_SULCOS);
			stmt.setLong(1, codUnidade);
			rSet = stmt.executeQuery();
			while(rSet.next()){
				valores.add(rSet.getDouble("ALTURA_SULCO_CENTRAL"));
				System.out.println(valores);
			}			
		}finally{
			closeConnection(conn, stmt, rSet);
		}
		setFaixas(resumo, valores);
		return resumo;
	};

	public List<ResumoSulcos.Faixa> setFaixas(ResumoSulcos resumo,List<Double> valores){
		Double minimo = (double) 0;
		Double cota = valores.get(0) / 5;
		Double maximo = cota;
		int totalPneus = valores.size();
		List<ResumoSulcos.Faixa> faixas = new ArrayList<>();

		while(minimo < valores.get(0)){
			ResumoSulcos.Faixa faixa = new ResumoSulcos.Faixa();
			faixa.inicio = minimo;
			faixa.fim = maximo;
			minimo = maximo;
			maximo = maximo + cota;
			faixas.add(faixa);
		}

		for(ResumoSulcos.Faixa faixa : faixas){
			for (int i = 0; i < valores.size(); i++) {
				if(valores.get(i)>= faixa.inicio && valores.get(i) <= faixa.fim){
					faixa.totalPneus ++;
					valores.remove(i);
					i--;
				}
			}
			System.out.println(faixa.totalPneus + "    " + totalPneus);
			faixa.porcentagem = faixa.totalPneus / totalPneus;
		}
		System.out.println(faixas);
		return faixas;
	}

	public void getResumoCalibragens(){};

}
