package br.com.zalf.prolog.webservice.integracao.transport;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 03/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface IntegracaoTransportDao {

    /**
     * Método utilizado para inserir no banco de dados do ProLog uma lista de
     * {@link List<ItemResolvidoIntegracaoTransport> itens resolvidos}. Estes itens são enviados através do sistema
     * integrado Transport, para serem salvos no ProLog.
     *
     * @param tokenIntegracao Token utilizado para a requisição. Este token será utilizado para
     *                        autenticar e descobrir o código da empresa que faz requisição.
     * @param dataHoraAtualUtc   Data e Hora atual, em UTC.
     * @param itensResolvidos Uma lista de {@link List<ItemResolvidoIntegracaoTransport> itens resolvidos}.
     * @throws Throwable Se algum erro ocorrer durante a resolução dos itens pendentes.
     */
    void resolverMultiplosItens(@NotNull final String tokenIntegracao,
                                @NotNull final LocalDateTime dataHoraAtualUtc,
                                @NotNull final List<ItemResolvidoIntegracaoTransport> itensResolvidos) throws Throwable;

    /**
     * Este método busca os {@link ItemPendenteIntegracaoTransport itens pendentes} a partir
     * do {@code codUltimoItemPendente} recebido.
     * O código do Último Item Pendente Sincronizado é utilizado como um Offset de busca,
     * todas os itens a partir deste código serão retornadas por este método.
     *
     * @param tokenIntegracao                   Token utilizado para a requisição. Este token será utilizado para
     *                                          descobrir qual empresa está requisitando as informações.
     * @param codUltimoItemPendenteSincronizado Código do último item pendente de Ordem de Serviço sincronizado.
     * @return Uma lista de {@link List <ItemPendenteIntegracaoTransport> itens pendentes} não sincronizados.
     * @throws Throwable Se algum erro ocorrer durante a busca dos itens pendentes.
     */
    @NotNull
    List<ItemPendenteIntegracaoTransport> getItensPendentes(
            @NotNull final String tokenIntegracao,
            @NotNull final Long codUltimoItemPendenteSincronizado) throws Throwable;
}