package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import br.com.zalf.prolog.models.Colaborador;
import br.com.zalf.prolog.models.Funcao;
import br.com.zalf.prolog.models.Request;

public interface ColaboradorDao {
	boolean insert(Colaborador colaborador) throws SQLException;
	boolean update(Request<Colaborador> request) throws SQLException;
	boolean delete(Request<Colaborador> request) throws SQLException;
	Colaborador getByCod(Long cpf, String token) throws SQLException;
	List<Colaborador> getAll(Request<?> request) throws SQLException;
	boolean verifyLogin(long cpf, Date dataNascimento) throws SQLException;
	Funcao getFuncaoByCod(Long codigo) throws SQLException;
	List<Colaborador> getAtivosByUnidade(Long codUnidade, String token, Long cpf) throws SQLException;
}
