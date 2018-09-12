package br.com.zalf.prolog.webservice.frota.checklist.ordemServico;

import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.ConsertoMultiplosItensOs;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.ItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.ManutencaoHolder;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.OrdemServico;
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
     * Conserta um {@link ItemOrdemServico item}.
     *
     * @param item item consertado
     * @throws Throwable se ocorrer algum erro
     */
    void consertaItem(@NotNull final ItemOrdemServico item) throws Throwable;

    /**
     * Conserta múltiplos {@link ItemOrdemServico itens}.
     *
     * @param itensConserto os itens para conserto
     * @throws Throwable se ocorrer algum erro
     */
    void consertaItens(@NotNull final ConsertoMultiplosItensOs itensConserto) throws Throwable;

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
                                      @NotNull final String statusItens,
                                      @NotNull final String prioridade,
                                      @Nullable final Integer limit,
                                      @Nullable final Long offset) throws SQLException;

	@NotNull
	List<ItemOrdemServico> getItensOs(@NotNull final Long codOs,
									  @NotNull final Long codUnidade,
									  @Nullable final String statusItemOs) throws Throwable;

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
}