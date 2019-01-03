package br.com.zalf.prolog.webservice.integracao.transport;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 03/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface IntegracaoTransportDao {

    /**
     * Este método busca os {@link ItemPendenteIntegracaoTransport itens pendentes} a partir
     * do {@code codUltimoItemPendente} recebido.
     * O código do Último Item Pendente Sincronizado é utilizado como um Offset de busca,
     * todas os itens a partir deste código serão retornadas por este método.
     *
     * @param tokenIntegracao       Token utilizado para a requisição. Este token será utilizado para
     *                              descobrir qual empresa está requisitando as informações.
     * @param codUltimoItemPendente Código do último item pendente de Ordem de Serviço sincronizado.
     * @return Uma lista de {@link List <ItemPendenteIntegracaoTransport> itens pendentes} não sincronizados.
     * @throws Throwable Se algum erro ocorrer durante a busca dos itens pendentes.
     */
    @NotNull
    List<ItemPendenteIntegracaoTransport> getItensPendentes(@NotNull final String tokenIntegracao,
                                                            @NotNull final Long codUltimoItemPendente) throws Throwable;
}
