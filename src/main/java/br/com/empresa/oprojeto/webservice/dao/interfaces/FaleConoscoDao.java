package br.com.empresa.oprojeto.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.empresa.oprojeto.models.FaleConosco;


public interface FaleConoscoDao {
	List<FaleConosco> getByColaborador(long cpf) throws SQLException;
}
