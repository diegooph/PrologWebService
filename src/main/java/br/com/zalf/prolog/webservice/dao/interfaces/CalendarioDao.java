package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Evento;

public interface CalendarioDao {
	
	public List<Evento> getEventosByCpf(Long cpf, String token) throws SQLException;

}
