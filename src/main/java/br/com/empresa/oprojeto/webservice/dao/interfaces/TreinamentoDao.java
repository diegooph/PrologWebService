package br.com.empresa.oprojeto.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.empresa.oprojeto.models.treinamento.Treinamento;


public interface TreinamentoDao {
	List<Treinamento> getNaoVistosColaborador(Long cpf) throws SQLException;
	List<Treinamento> getVistosColaborador(Long cpf) throws SQLException;
}
