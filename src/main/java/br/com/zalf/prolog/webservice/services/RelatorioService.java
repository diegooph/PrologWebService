package br.com.zalf.prolog.webservice.services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import br.com.zalf.prolog.models.relatorios.ConsolidadoHolder;
import br.com.zalf.prolog.models.relatorios.Empresa;
import br.com.zalf.prolog.webservice.dao.RelatorioDaoImpl;

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
			int codUnidade, Long cpf, String token){
		try{
			return dao.getRelatorioByPeriodo(dataInicial, dataFinal, equipe, codUnidade, cpf, token);
		}catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
