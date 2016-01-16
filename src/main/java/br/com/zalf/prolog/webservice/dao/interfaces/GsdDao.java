package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.gsd.Gsd;

public interface GsdDao {
	List<Gsd> getByColaborador(Long cpf) throws SQLException;
	List<Gsd> getByAvaliador(Long cpf) throws SQLException;
}
