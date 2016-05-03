package br.com.zalf.prolog.webservice.calendario;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Evento;

public class CalendarioService {

	private CalendarioDaoImpl dao = new CalendarioDaoImpl();
	
	public List<Evento> getEventosByCpf(Long cpf, String token){
		try{
			return dao.getEventosByCpf(cpf, token);
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

}
