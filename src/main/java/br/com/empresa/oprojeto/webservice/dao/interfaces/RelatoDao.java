package br.com.empresa.oprojeto.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.empresa.oprojeto.webservice.domain.Relato;

public interface RelatoDao {
	List<Relato> getRelatosByColaborador(long cpf) throws SQLException;
}
