package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Relato;
import br.com.zalf.prolog.models.Request;


public interface RelatoDao {
	boolean insert(Relato relato) throws SQLException;
	boolean update(Request<Relato> request) throws SQLException;
	boolean delete(Request<Relato> request) throws SQLException;
	Relato getByCod(Request<?> request) throws SQLException;
	List<Relato> getAll(Request<?> request) throws SQLException;
	List<Relato> getByColaborador(Long cpf, String token, long offset, double latitude, double longitude) throws SQLException;
	List<Relato> getAllExcetoColaborador(Long cpf, String token, long offset, double latitude, double longitude) throws SQLException;
}
