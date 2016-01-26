package br.com.zalf.prolog.webservice.services;

import java.sql.SQLException;
import java.time.LocalDate;

import br.com.zalf.prolog.models.relatorios.ConsolidadoHolder;
import br.com.zalf.prolog.webservice.dao.RelatorioDaoImpl;

public class RelatorioService {

	private RelatorioDaoImpl dao = new RelatorioDaoImpl();

	public ConsolidadoHolder getIndicadoresEquipeByPeriodo(LocalDate dataInicial, LocalDate dataFinal, String equipe,
			int codUnidade, Long cpf, String token){
		try{
			return dao.getIndicadoresEquipeByPeriodo(dataInicial, dataFinal, equipe, codUnidade, cpf, token);
		}catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ConsolidadoHolder getIndicadoresUnidadeByPeriodo(LocalDate dataInicial, LocalDate dataFinal,
			int codUnidade, Long cpf, String token){
		try{
			return dao.getIndicadoresUnidadeByPeriodo(dataInicial, dataFinal, codUnidade, cpf, token);
		}catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
