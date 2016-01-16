package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.Date;

import br.com.zalf.prolog.models.Funcao;

public interface ColaboradorDao {
	boolean verifyLogin(long cpf, Date dataNascimento) throws SQLException;
	Funcao getFuncaoByCod(Long codigo) throws SQLException;
}
