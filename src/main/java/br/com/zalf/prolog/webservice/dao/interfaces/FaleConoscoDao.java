package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.FaleConosco;


public interface FaleConoscoDao {
	List<FaleConosco> getByColaborador(long cpf) throws SQLException;
}
