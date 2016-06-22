package br.com.zalf.prolog.webservice.gente.calendario;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Evento;

public class CalendarioService {

	private CalendarioDaoImpl dao = new CalendarioDaoImpl();
	
	public List<Evento> getEventosByCpf(Long cpf){
		try{
			return dao.getEventosByCpf(cpf);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

}
