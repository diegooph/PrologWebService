package br.com.empresa.oprojeto.webservice.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

import br.com.empresa.oprojeto.models.Veiculo;


public interface VeiculoDao {
	List<Veiculo> getVeiculosAtivosByUnidade (Long codUnidade) 
			throws SQLException;
}
