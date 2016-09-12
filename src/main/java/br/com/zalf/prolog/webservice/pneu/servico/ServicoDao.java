package br.com.zalf.prolog.webservice.pneu.servico;

import br.com.zalf.prolog.frota.pneu.servico.PlacaServicoHolder;

import java.sql.SQLException;

public interface ServicoDao {
	
	public PlacaServicoHolder getPlacasServico(Long codUnidade) throws SQLException;

}
