package br.com.zalf.prolog.webservice.services;

import java.sql.SQLException;
import java.time.LocalDate;

import br.com.zalf.prolog.models.dashboard.DashSeguranca;
import br.com.zalf.prolog.webservice.DashManager;

public class DashService {

	DashManager dashManager = new DashManager();

	public DashSeguranca getDashSeguranca(LocalDate dataInicial, LocalDate dataFinal, Long codUnidade, String equipe){
		try{
			return dashManager.getDashSegurancaDaoImpl().getDashSeguranca(dataInicial, dataFinal, codUnidade, equipe);
		}
		catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

}
