package br.com.empresa.oprojeto.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.empresa.oprojeto.webservice.domain.FaleConosco;

public interface FaleConoscoDao {
	List<FaleConosco> getPorColaborador(long cpf) throws SQLException;
}
