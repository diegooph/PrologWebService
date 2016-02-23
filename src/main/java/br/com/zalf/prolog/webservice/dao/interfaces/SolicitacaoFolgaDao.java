package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Request;
import br.com.zalf.prolog.models.SolicitacaoFolga;

public interface SolicitacaoFolgaDao {
	boolean insert(SolicitacaoFolga relato) throws SQLException;
	boolean update(Request<SolicitacaoFolga> request) throws SQLException;
	boolean delete(Request<SolicitacaoFolga> request) throws SQLException;
	SolicitacaoFolga getByCod(Request<?> request) throws SQLException;
	List<SolicitacaoFolga> getAll(Request<?> request) throws SQLException;
	List<SolicitacaoFolga> getByColaborador(Long cpf, String token) throws SQLException;
}
