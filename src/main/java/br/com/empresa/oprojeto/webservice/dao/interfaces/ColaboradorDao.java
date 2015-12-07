package br.com.empresa.oprojeto.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.Date;

public interface ColaboradorDao {
	boolean verifyLogin(long cpf, Date dataNascimento) throws SQLException;
}
