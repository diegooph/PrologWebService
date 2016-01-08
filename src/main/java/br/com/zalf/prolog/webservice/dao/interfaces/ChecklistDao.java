package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.empresa.oprojeto.models.Pergunta;
import br.com.empresa.oprojeto.models.checklist.Checklist;

public interface ChecklistDao {
	List<Checklist> getByColaborador(Long cpf) throws SQLException;
	List<Pergunta> getPerguntas() throws SQLException;
}
