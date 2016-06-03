package br.com.zalf.prolog.webservice.servico;

import java.sql.SQLException;

import br.com.zalf.prolog.models.PlacaServicoHolder;

public interface ServicoDao {
	
	public PlacaServicoHolder getPlacasServico(Long codUnidade) throws SQLException;

}
