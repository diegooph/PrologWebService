package br.com.zalf.prolog.webservice.frota.checklist.ordemServico;

import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by didi on 9/16/16.
 */
public interface OrdemServicoDao {

	/**
	 * busca OS - Orde de Serviço - na banco
	 * @param placa placa do veículo
	 * @param status status
	 * @param codUnidade código da unidade
	 * @param tipoVeiculo tipo de veículo
	 * @param limit limit de busca no banco
	 * @param offset offset de busca no banco
	 * @return uma lista de Ordens de serviços
	 * @throws SQLException caso ocorrer erro no banco
	 */
	List<OrdemServico> getOs(String placa, String status, Long codUnidade,
							 String tipoVeiculo, Integer limit, Long offset) throws SQLException;

	@NotNull
	List<ItemOrdemServico> getItensOs(@NotNull final String placa,
									  @NotNull final String status,
									  @NotNull final String prioridade,
									  @Nullable final Integer limit,
									  @Nullable final Long offset) throws SQLException;

	List<ItemOrdemServico> getItensOs(@NotNull final String placa,
									  @NotNull final Date untilDate,
									  @NotNull final ItemOrdemServico.Status statusItem,
									  @NotNull final String prioridadeItem,
									  final boolean itensCriticosRetroativos) throws SQLException;

	/**
	 * insere um item com problema na OS
	 * @param checklist um checklist
	 * @param conn conexão do banco
	 * @param codUnidade código da unidade
	 * @throws SQLException se ocorrer erro no banco
	 */
	void insertItemOs(Checklist checklist, Connection conn, Long codUnidade) throws SQLException;

	/**
	 * Busca a lista de itens agrupadas por placa e criticidade (tela das bolinhas).
	 */
	@NotNull
	List<ManutencaoHolder> getResumoManutencaoHolder(@NotNull final Long codUnidade,
													 @Nullable final Long codTipoVeiculo,
													 @Nullable final String placaVeiculo,
													 final boolean itensEmAberto,
													 final int limit,
													 final int offset) throws Throwable;

	/**
	 * marca um item como consertado
	 * @param item item consertado
	 * @param placa placa do veículo
	 * @return valor da operação
	 * @throws SQLException se ocorrer erro no banco
	 */
	boolean consertaItem (ItemOrdemServico item, String placa) throws SQLException;

}
