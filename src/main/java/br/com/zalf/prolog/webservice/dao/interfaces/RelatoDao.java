package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.empresa.oprojeto.models.Relato;


public interface RelatoDao {
	List<Relato> getByColaborador(Long cpf) throws SQLException;
	List<Relato> getAllExcetoColaborador(Long cpf) throws SQLException;
}
