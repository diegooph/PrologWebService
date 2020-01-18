package br.com.zalf.prolog.webservice.frota.checklist.ordemservico;

import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.InfosAlternativaAberturaOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem.OrdemServicoListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem.QtdItensPlacaListagem;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.HolderResolucaoItensOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.HolderResolucaoOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.resolucao.ResolverMultiplosItensOs;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Created on 20/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface OrdemServicoDao {

    /**
     * Processa o checklist que foi realizado pelo usuário e cria itens de ordens de serviços ou incrementa a quantidade
     * de apontamento para itens já existentes e ainda pendentes.
     *
     * @param conn      Conexão com o banco atualmente aberta.
     * @param checklist O checklist que foi realizado.
     * @throws Throwable Se ocorrer algum erro no processamento das informações.
     */
    void processaChecklistRealizado(@NotNull final Connection conn,
                                    @NotNull final Long codChecklistInserido,
                                    @NotNull final ChecklistInsercao checklist) throws Throwable;

    /**
     * Método utilizado para buscar a Listagem de Ordens de Serviços.
     * Este método é utilizado tanto para a busca das Ordens de Serviços Abertas quanto as Fechadas,
     * o que define qual será buscada é o parâmetro {@code statusOrdemServico} que pode ser
     * {@link StatusOrdemServico#ABERTA} ou {@link StatusOrdemServico#FECHADA}.
     * <p>
     * Para buscar as ordens de serviços de TODOS os veículos o parâmetro {@code placa} deve ser <code>NULL</code>.
     * O mesmo acontece com o parâmetro {@code tipoVeiculo}.
     * <p>
     * Esta busca utiliza paginação, então deve-se explicitar a quantidade de dados que serão buscados
     * através dos parâmetros {@code limit} e {@code offset}.
     *
     * @param codUnidade         Código da {@link Unidade} que as Ordens de Serviço pertencem.
     * @param codTipoVeiculo     Tipo de Veículo que deseja-se buscar as Ordens de Serviço.
     * @param placa              Placa do Veículo que deseja-se buscar as Ordens de Serviço.
     * @param statusOrdemServico Status em que a Ordem de Serviço se encontra.
     * @param limit              Quantidade de elementos a serem retornados na busca.
     * @param offset             Indice a partir do qual a busca será retornada.
     * @return Lista de {@link List<OrdemServicoListagem> ordens de serviço}, seguindo a fltragem aplicada.
     * @throws Throwable Se algum erro no processamento da busca ocorrer.
     */
    @NotNull
    List<OrdemServicoListagem> getOrdemServicoListagem(@NotNull final Long codUnidade,
                                                       @Nullable final Long codTipoVeiculo,
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
     * @param codUnidade     Código da {@link Unidade} que os Itens pertencem.
     * @param codTipoVeiculo Tipo de Veículo que deseja-se buscar as Ordens de Serviço.
     * @param placaVeiculo   Placa do Veículo que deseja-se contar os Itens.
     * @param statusItens    Status em que o Item se encontra.
     * @param limit          Quantidade de elementos a serem retornados na busca.
     * @param offset         Indice a partir do qual a busca será retornada.
     * @return Lista de {@link List<QtdItensPlacaListagem> quantidade de itens por placa}, seguindo a filtragem.
     * @throws Throwable Se ocorrer algum erro no processamento dos dados.
     */
    @NotNull
    List<QtdItensPlacaListagem> getQtdItensPlacaListagem(@NotNull final Long codUnidade,
                                                         @Nullable final Long codTipoVeiculo,
                                                         @Nullable final String placaVeiculo,
                                                         @Nullable final StatusItemOrdemServico statusItens,
                                                         final int limit,
                                                         final int offset) throws Throwable;

    /**
     * Método utilizado para buscar uma Ordem de Serviço para ser fechada.
     *
     * @param codUnidade      Código da {@link Unidade} que a Ordem de Serviço pertence.
     * @param codOrdemServico Código da Ordem de Serviço.
     * @return Um {@link HolderResolucaoOrdemServico holder} contendo a Ordem de Serviço para o fechamento.
     * @throws Throwable Caso algum erro acontecer no processo de busca.
     */
    @NotNull
    HolderResolucaoOrdemServico getHolderResolucaoOrdemServico(@NotNull final Long codUnidade,
                                                               @NotNull final Long codOrdemServico) throws Throwable;

    /**
     * Método utilizado para buscar os Itens de uma Ordem de Serviço para serem resolvidos.
     *
     * @param placaVeiculo Placa do Veículo a qual os Itens pertencem.
     * @param prioridade   Prioridade dos Itens que serão buscados.
     * @param statusItens  Status dos Itens que serão buscados.
     * @param limit        Quantidade de elementos a serem retornados na busca.
     * @param offset       Indice a partir do qual a busca será retornada.
     * @return Um {@link HolderResolucaoItensOrdemServico holder} contendo os Itens que serão resolvidos.
     * @throws Throwable Se algum erro acontecer na busca dos dados.
     */
    @NotNull
    HolderResolucaoItensOrdemServico getHolderResolucaoItensOrdemServico(
            @NotNull final String placaVeiculo,
            @Nullable final PrioridadeAlternativa prioridade,
            @Nullable final StatusItemOrdemServico statusItens,
            final int limit,
            final int offset) throws Throwable;

    /**
     * Método genérico para a busca de Itens de Ordem de Serviço. Todos os parâmetros de filtragem
     * são assinados com a anotação {@link Nullable} indicando que podem ser <code>Null</code>.
     * Mas a combinação de todos os parâmetros <code>Null</code> não pode acontecer.
     * <p>
     * A filtragem de Itens de Ordem de Serviço têm que acontecer através da {@code placaVeiculo}
     * ou através da combinação de {@code codUnidade} e {@code codOrdemServico}.
     * <p>
     * O parâmetro {@code statusItens} é utilizado para filtrar o resultado entre
     * {@link StatusItemOrdemServico#RESOLVIDO} ou {@link StatusItemOrdemServico#PENDENTE}, para que a busca
     * retorne ambos os status é necessário que {@code statusItens} seja <code>Null</code>.
     *
     * @param codUnidade      Código da {@link Unidade} de filtragem dos Itens de Ordem de Serviço.
     * @param codOrdemServico Código da Ordem de Serviço para filtrar os Itens.
     * @param placaVeiculo    Placa do {@link Veiculo} para filtrar os Itens de Ordem de Serviço.
     * @param statusItens     {@link StatusItemOrdemServico status} de filtragem do Itens.
     * @return Um {@link HolderResolucaoItensOrdemServico holder} contendo os Itens de Ordem de Serviço filtrados.
     * @throws Throwable Se algum erro ocorrer na realização da busca das informações.
     */
    @NotNull
    HolderResolucaoItensOrdemServico getHolderResolucaoMultiplosItens(
            @Nullable final Long codUnidade,
            @Nullable final Long codOrdemServico,
            @Nullable final String placaVeiculo,
            @Nullable final StatusItemOrdemServico statusItens) throws Throwable;

    /**
     * Método responsável por resolver um item de uma Ordem de Serviço.
     *
     * @param item Objeto {@link ResolverItemOrdemServico} contendo as informações da item resolvido.
     * @throws Throwable Se ocorrer algum erro no processamento das informações.
     */
    void resolverItem(@NotNull final ResolverItemOrdemServico item) throws Throwable;

    /**
     * Método responsável por resolver múltiplos itens de uma Ordem de Serviço.
     *
     * @param itensResolucao Objeto {@link ResolverMultiplosItensOs} contendo as informações dos itens resolvidos.
     * @throws Throwable Se ocorrer algum erro no processamento das informações.
     */
    void resolverItens(@NotNull final ResolverMultiplosItensOs itensResolucao) throws Throwable;

    /**
     * Método utilizado para incrementar a quantidade de apontamentos de uma lista de códigos de itens de Ordem de
     * Serviço.
     *
     * @param conn                             Conexão com o bando para buscar os dados.
     * @param codChecklistInserido             Código do checklist que está sendo processado.
     * @param itensOsIncrementaQtdApontamentos Itens de O.S que deverão ter suas quantidade de apontamentos atualizadas.
     * @throws Throwable Se algum erro acontecer no processo de atualização.
     */
    void incrementaQtdApontamentos(
            @NotNull final Connection conn,
            @NotNull final Long codChecklistInserido,
            @NotNull final List<InfosAlternativaAberturaOrdemServico> itensOsIncrementaQtdApontamentos)
            throws Throwable;

    /**
     * Método responsável por buscar o 'status das alternativas' de um modelo de checklist. O Status da alternativa
     * consiste, neste contexto, nas informações de Ordens de Serviço Abertas para cada alternativa.
     * O método recebe como parâmetro o {@code codModelo código do modelo} a qual as alternativas serão analisadas e
     * também a {@code placaVeiculo placa do veículo} que será utilizada como base para saber se tem algum serviço
     * pendente.
     * Para cada alternativa do modelo, o método irá verificar se existe algum serviço para ser realizado na placa com
     * o mesmo código de alternativa.
     *
     * @param conn         Conexão com o bando para buscar os dados.
     * @param codModelo    Código do modelo de checklist para analisar as alternativas.
     * @param placaVeiculo Placa do veículo para buscar itens em aberto.
     * @return Um dicionário de informações listando o código das anternativas e qual as informações referentes.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Map<Long, List<InfosAlternativaAberturaOrdemServico>> getItensStatus(
            @NotNull final Connection conn,
            @NotNull final Long codModelo,
            @NotNull final Long codVersaoModelo,
            @NotNull final String placaVeiculo) throws Throwable;
}