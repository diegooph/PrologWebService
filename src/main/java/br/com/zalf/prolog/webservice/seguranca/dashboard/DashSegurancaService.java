package br.com.zalf.prolog.webservice.seguranca.dashboard;

import br.com.zalf.prolog.seguranca.dashboard.DashSeguranca;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Classe DashSegurancaService responsavel por comunicar-se com a interface DAO
 */
public class DashSegurancaService {

	private DashSegurancaDao dao = new DashSegurancaDaoImpl();

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
