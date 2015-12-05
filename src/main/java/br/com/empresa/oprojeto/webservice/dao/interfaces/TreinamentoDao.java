package br.com.empresa.oprojeto.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.empresa.oprojeto.webservice.domain.treinamento.Treinamento;

public interface TreinamentoDao {
	List<Treinamento> getNaoVistosColaborador(long cpf) throws SQLException;
	List<Treinamento> getVistosColaborador(long cpf) throws SQLException;
}
