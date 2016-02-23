package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.FaleConosco;
import br.com.zalf.prolog.models.Request;


public interface FaleConoscoDao {
	boolean insert(FaleConosco faleConosco) throws SQLException;
	boolean update(Request<FaleConosco> request) throws SQLException;
	boolean delete(Request<FaleConosco> request) throws SQLException;
	FaleConosco getByCod(Request<?> request) throws SQLException;
	List<FaleConosco> getAll(Request<?> request) throws SQLException;
	List<FaleConosco> getByColaborador(long cpf) throws SQLException;
}
