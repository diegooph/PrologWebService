package br.com.zalf.oprojeto.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.Date;

import br.com.empresa.oprojeto.models.Funcao;

public interface ColaboradorDao {
	boolean verifyLogin(long cpf, Date dataNascimento) throws SQLException;
	Funcao getFuncaoByCod(Long codigo) throws SQLException;
}
