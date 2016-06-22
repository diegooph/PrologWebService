package br.com.zalf.prolog.webservice.seguranca.dashboard;

import java.sql.SQLException;
import java.time.LocalDate;

import br.com.zalf.prolog.models.dashboard.DashSeguranca;

public class DashSegurancaService {

	DashSegurancaDaoImpl dao = new DashSegurancaDaoImpl();

	public DashSeguranca getDashSeguranca(LocalDate dataInicial, LocalDate dataFinal, Long codUnidade, String equipe){
		try{
			return dao.getDashSeguranca(dataInicial, dataFinal, codUnidade, equipe);
		}
		catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

}
