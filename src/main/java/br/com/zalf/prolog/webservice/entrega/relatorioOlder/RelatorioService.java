package br.com.zalf.prolog.webservice.entrega.relatorioOlder;


import br.com.zalf.prolog.entrega.relatorio.older.ConsolidadoHolder;

import java.sql.SQLException;
import java.time.LocalDate;

public class RelatorioService {

	private RelatorioDaoImpl dao = new RelatorioDaoImpl();
	


	public ConsolidadoHolder getRelatorioByPeriodo(LocalDate dataInicial, LocalDate dataFinal, String equipe,
												   Long codUnidade, Long cpf, String token){
		try{
			return dao.getRelatorioByPeriodo(dataInicial, dataFinal, equipe, codUnidade, cpf, token);
		}catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
