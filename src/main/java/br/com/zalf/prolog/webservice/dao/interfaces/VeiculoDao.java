package br.com.zalf.prolog.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.zalf.prolog.models.Veiculo;


public interface VeiculoDao {
	List<Veiculo> getVeiculosAtivosByUnidade(Long codUnidade) 
			throws SQLException;
	
	List<Veiculo> getVeiculosAtivosByUnidadeByColaborador(Long cpf, String token) 
			throws SQLException;
}
