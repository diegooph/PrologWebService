package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;

import br.com.zalf.prolog.models.Autenticacao;

public interface AutenticacaoDao {
	Autenticacao insertOrUpdate(Long cpf) throws SQLException;
	boolean verifyIfExists(Autenticacao autenticacao) throws SQLException;
	boolean delete(Autenticacao autenticacao) throws SQLException;
}
