package br.com.zalf.prolog.webservice.services;

import java.sql.SQLException;
import java.time.LocalDate;

import br.com.zalf.prolog.models.relatorios.ConsolidadoHolder;
import br.com.zalf.prolog.webservice.dao.RelatorioDaoImpl;

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
