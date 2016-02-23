package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Pergunta;
import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.checklist.Checklist;

public interface ChecklistDao {
	boolean insert(Checklist checklist) throws SQLException;
	boolean update(Request<Checklist> request) throws SQLException;
	boolean delete(Request<Checklist> request) throws SQLException;
	Checklist getByCod(Request<?> request) throws SQLException;
	List<Checklist> getAll(Request<?> request) throws SQLException;
	List<Checklist> getByColaborador(Long cpf, String token, long offset) throws SQLException;
	List<Pergunta> getPerguntas() throws SQLException;
	List<Checklist> getAllExcetoColaborador(Long cpf, long offset) throws SQLException;
}
