package br.com.empresa.oprojeto.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.empresa.oprojeto.models.checklist.Checklist;

public interface ChecklistDao {
	List<Checklist> getByColaborador(Long cpf) throws SQLException;
	void insertRespostas(Checklist checklist) throws SQLException;
}
