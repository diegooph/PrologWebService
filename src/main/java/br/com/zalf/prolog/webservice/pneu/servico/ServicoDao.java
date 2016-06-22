package br.com.zalf.prolog.webservice.pneu.servico;

import java.sql.SQLException;

import br.com.zalf.prolog.models.pneu.servico.PlacaServicoHolder;

public interface ServicoDao {
	
	public PlacaServicoHolder getPlacasServico(Long codUnidade) throws SQLException;

}
