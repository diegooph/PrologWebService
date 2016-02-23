package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Pergunta;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.gsd.Gsd;

public interface GsdDao {
	boolean insert(Gsd gsd) throws SQLException;
	boolean update(Request<Gsd> request) throws SQLException;
	boolean delete(Request<Gsd> request) throws SQLException;
	Gsd getByCod(Request<?> request) throws SQLException;
	List<Gsd> getAll(Request<?> request) throws SQLException;
	List<Gsd> getByColaborador(Long cpf, String token) throws SQLException;
	List<Gsd> getByAvaliador(Long cpf, String token) throws SQLException;
	List<Pergunta> getPerguntas() throws SQLException;
	List<Gsd> getAllExcetoAvaliador(Long cpf, String token) throws SQLException;
}
