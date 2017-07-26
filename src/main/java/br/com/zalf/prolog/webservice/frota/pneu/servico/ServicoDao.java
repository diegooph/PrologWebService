package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.frota.pneu.servico.model.PlacaServicoHolder;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.Servico;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.ServicoHolder;

import java.sql.SQLException;
import java.util.List;

public interface ServicoDao {

	/**
	 * busca as placas com serviços da unidade
	 * @param codUnidade código da unidade
	 * @return placas com serviços
	 * @throws SQLException se ocorrer erro no banco de dados
	 */
	PlacaServicoHolder getPlacasServico(Long codUnidade) throws SQLException;

	/**
	 * busca os serviços referentes a uma placa especifica
	 * @param placa placa do veículo
	 * @param codUnidade código da unidade
	 * @return serviços da placa
	 * @throws SQLException se ocorrer erro no banco de dados
	 */
	ServicoHolder getServicosByPlaca (String placa, Long codUnidade) throws SQLException;

	/**
	 * busca os serviços abertos de uma placa
	 * @param placa placa do veículo
	 * @param tipoServico tipo do serviço
	 * @return lista de serviços da placa
	 * @throws SQLException se ocorrer erro no banco de dados
	 */
	List<Servico> getServicosAbertosByPlaca(String placa, String tipoServico) throws SQLException;

	/**
	 * insere um serviço
	 * @param servico um serviço
	 * @param codUnidade código da unidade
	 * @return valor da operação
	 * @throws SQLException se ocorrer erro no banco de dados
	 */
	boolean insertManutencao(Servico servico, Long codUnidade) throws SQLException;

}
