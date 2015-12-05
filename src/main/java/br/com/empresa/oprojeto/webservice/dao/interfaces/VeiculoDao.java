package br.com.empresa.oprojeto.webservice.dao.interfaces;

import java.util.List;

import br.com.empresa.oprojeto.webservice.domain.Veiculo;

public interface VeiculoDao {
	
	List<Veiculo> getVeiculosAtivosByUnidade (Long codUnidade);

}
