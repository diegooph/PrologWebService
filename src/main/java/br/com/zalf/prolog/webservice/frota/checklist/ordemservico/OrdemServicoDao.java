package br.com.zalf.prolog.webservice.frota.checklist.ordemservico;

import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem.OrdemServicoListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem.QtdItensPlacaListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.HolderResolucaoItensOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.HolderResolucaoOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.util.List;

/**
 * Created on 20/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface OrdemServicoDao {

    /**
     * Método chamado quando é recebido um checklist, verifica as premissas para criar uma nova OS ou add
     * o item com problema a uma OS existente.
     *
     * @param checklist  um checklist
     * @param conn       conexão do banco
     * @param codUnidade código da unidade
     * @throws Throwable se ocorrer algum erro.
     */
    void criarItemOrdemServico(@NotNull final Connection conn,
                               @NotNull final Long codUnidade,
                               @NotNull final Checklist checklist) throws Throwable;

    /**
     * Busca as ordens de serviços.
     *
     * @throws Throwable se ocorrer algum erro.
     **/
    @NotNull
    List<OrdemServicoListagem> getOrdemServicoListagem(@NotNull final Long codUnidade,
                                                       @Nullable final Long tipoVeiculo,
                                                       @Nullable final String placa,
                                                       @Nullable final StatusOrdemServico statusOrdemServico,
                                                       final int limit,
                                                       final int offset) throws Throwable;

    /**
     * Método utilizado para buscar a quantidade de Itens apontados como Não Ok (NOK) em uma Placa,
     * agrupando por {@link PrioridadeAlternativa prioridade} do Item. A quantidade de Itens pode ser filtrada
     * pelo status em que o Item se encontra, podendo ser {@link StatusItemOrdemServico#PENDENTE} ou
     * {@link StatusItemOrdemServico#RESOLVIDO};
     * <p>
     * Para buscar a quantidade de Itens apontados de TODAS os veículos
     * o parâmetro {@code placa} deve ser <code>NULL</code>.
     * <p>
     * Esta busca utiliza paginação, então deve-se explicitar a quantidade de dados que serão buscados
     * através dos parâmetros {@code limit} e {@code offset}.
     *
     * @param codUnidade             Código da {@link Unidade} que os Itens pertencem.
     * @param codTipoVeiculo         Tipo de Veículo que deseja-se buscar as Ordens de Serviço.
     * @param placaVeiculo           Placa do Veículo que deseja-se contar os Itens.
     * @param statusItemOrdemServico Status em que o Item se encontra.
     * @param limit                  Quantidade de elementos a serem retornados na busca.
     * @param offset                 Indice a partir do qual a busca será retornada.
     * @return Lista de {@link List<QtdItensPlacaListagem> quantidade de itens por placa}, seguindo a filtragem.
     * @throws Throwable Se ocorrer algum erro no processamento dos dados.
     */
    @NotNull
    List<QtdItensPlacaListagem> getQtdItensPlacaListagem(@NotNull final Long codUnidade,
                                                         @Nullable final Long codTipoVeiculo,
                                                         @Nullable final String placaVeiculo,
                                                         @Nullable final StatusItemOrdemServico statusItemOrdemServico,
                                                         final int limit,
                                                         final int offset) throws Throwable;

    /**
     * Busca os itens de Ordens de Serviços utilizando, opcionalmente, limit e offset.
     *
     * @throws Throwable se ocorrer algum erro.
     */
    @NotNull
    HolderResolucaoOrdemServico getHolderResolucaoOrdemServico(@NotNull final Long codOrdemServico,
                                                               @NotNull final Long codUnidade) throws Throwable;

    @NotNull
    HolderResolucaoItensOrdemServico getHolderResolucaoItensOrdemServico(
            @NotNull final String placaVeiculo,
            @NotNull final PrioridadeAlternativa prioridade) throws Throwable;

    /**
     * Resolve um item.
     *
     * @param item item resolvido.
     * @throws Throwable se ocorrer algum erro.
     */
    void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable;

    /**
     * Resolve múltiplos itens de uma Ordem de Serviço.
     *
     * @param itensResolucao os itens para resolução.
     * @throws Throwable se ocorrer algum erro.
     */
    void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable;
}