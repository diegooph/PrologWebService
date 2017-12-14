package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.OrigemDestinoInvalidaException;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ServicoDao {

	Long criaServico(String pneu, Long codAfericao, TipoServico tipoServico, Long codUnidade, Connection conn)
			throws SQLException;

	void incrementaQtdApontamentosServico(String codPneu, Long codUnidade, TipoServico tipoServico, Connection conn) throws SQLException;

	/**
	 * Método usado para trocar um serviço cadastrado como calibragem para inspeção.
	 */
	void calibragemToInspecao(String codPneu, Long codUnidade, Connection conn) throws SQLException;

	List<TipoServico> getServicosCadastradosByPneu(String codPneu, Long codUnidade) throws SQLException;

	ServicosAbertosHolder getQuantidadeServicosAbertosVeiculo(Long codUnidade) throws SQLException;

	ServicoHolder getServicoHolder(String placa, Long codUnidade) throws SQLException;

	List<Servico> getServicosAbertosByPlaca(@NotNull String placa, @Nullable TipoServico tipoServico) throws SQLException;

	void consertaServico(Servico servico, Long codUnidade) throws SQLException, OrigemDestinoInvalidaException;

	Servico getServicoByCod(final Long codUnidade, final Long codServico) throws SQLException;

	ServicosFechadosHolder getQuantidadeServicosFechadosByVeiculo(final Long codUnidade,
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

	int getQuantidadeServicosEmAbertoPneu(final Long codUnidade, final String codPneu) throws SQLException;


	/**
	 * Remonta um veículo como ele estava na época da abertura do {@link Servico}. Com todos os seus pneus
	 * ({@link Pneu}) na posição onde estavam na época e com os valores de sulco e pressão setados.
	 */
	@NotNull
	Veiculo getVeiculoAberturaServico(@NotNull final Long codServico, @NotNull final String placaVeiculo)
			throws SQLException;
}