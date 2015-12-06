package br.com.empresa.oprojeto.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.empresa.oprojeto.models.Relato;


public interface RelatoDao {
	List<Relato> getRelatosByColaborador(Long cpf) throws SQLException;
}
