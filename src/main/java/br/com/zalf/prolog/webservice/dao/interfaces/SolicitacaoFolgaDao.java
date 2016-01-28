package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.SolicitacaoFolga;

public interface SolicitacaoFolgaDao {
	List<SolicitacaoFolga> getByColaborador(Long cpf, String token) throws SQLException;
}
