package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.OrigemDestinoInvalidaException;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;

public interface ServicoDao {

	ServicosAbertosHolder getQuantidadeServicosAbertosVeiculo(Long codUnidade) throws SQLException;

	ServicoHolder getServicoHolder(String placa, Long codUnidade) throws SQLException;

	/**
	 * busca os serviços abertos de uma placa
	 * @param placa placa do veículo
	 * @param tipoServico tipo do serviço
	 * @return lista de serviços da placa
	 * @throws SQLException se ocorrer erro no banco de dados
	 */
	List<Servico> getServicosAbertosByPlaca(@NotNull String placa, @Nullable TipoServico tipoServico) throws SQLException;

	/**
	 * insere um serviço
	 * @param servico um serviço
	 * @param codUnidade código da unidade
	 * @return valor da operação
	 * @throws SQLException se ocorrer erro no banco de dados
	 */
	void insertManutencao(Servico servico, Long codUnidade) throws SQLException, OrigemDestinoInvalidaException;

	Servico getServicoByCod(final Long codUnidade, final Long codServico) throws SQLException;

	ServicosFechadosHolder getQuantidadeServicosFechadosByPlaca(final Long codUnidade,
																final long dataInicial,
																final long dataFinal) throws SQLException;


	ServicosFechadosHolder getQuantidadeServicosFechadosByPneu(final Long codUnidade,
															   final long dataInicial,
															   final long dataFinal) throws SQLException;

	List<Servico> getServicosFechados(final Long codUnidade,
									  final long dataInicial,
									  final long dataFinal) throws SQLException;

	/**
	 * Retorna os serviços fechados referentes ao pneu com código {@code codPneu}.
	 */
	List<Servico> getServicosFechadosPneu(final Long codUnidade,
										  final String codPneu,
										  final long dataInicial,
										  final long dataFinal) throws SQLException;

	/**
	 * Retorna os serviços fechados referentes ao veículo com placa {@code placaVeiculo}.
	 */
	List<Servico> getServicosFechadosVeiculo(final Long codUnidade,
											 final String placaVeiculo,
											 final long dataInicial,
											 final long dataFinal) throws SQLException;
}