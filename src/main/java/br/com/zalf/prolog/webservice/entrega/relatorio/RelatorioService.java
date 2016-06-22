package br.com.zalf.prolog.webservice.entrega.relatorio;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import br.com.zalf.prolog.models.relatorios.ConsolidadoHolder;
import br.com.zalf.prolog.models.relatorios.Empresa;

public class RelatorioService {

	private RelatorioDaoImpl dao = new RelatorioDaoImpl();
	
	public List<Empresa> getFiltros(Long cpf, String token){
		try{
			return dao.getFiltros(cpf, token);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

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
