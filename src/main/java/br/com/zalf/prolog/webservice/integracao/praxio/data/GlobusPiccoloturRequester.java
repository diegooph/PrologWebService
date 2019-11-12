package br.com.zalf.prolog.webservice.integracao.praxio.data;

import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.ProcessoMovimentacaoGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.OrdemDeServicoCorretivaPrologVO;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 01/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface GlobusPiccoloturRequester {

    /**
     * Método utilizado para enviar ao Globus os itens <code>NOK</code> apontados no checklist.
     * <p>
     * Só serão enviados os itens que devem abrir um Ordem de Serviço dentro do Globus. Itens que já possuem uma O.S não
     * serão enviados, para estes, apenas será incrementado a quantidade de apontamentos.
     *
     * @param ordemDeServicoCorretivaPrologVO Objeto que encapsula as informações para enviar ao endpoint do Globus.
     * @return O código da Ordem de Serviço que foi criada no Globus.
     */
    @NotNull
    Long insertItensNok(@NotNull final OrdemDeServicoCorretivaPrologVO ordemDeServicoCorretivaPrologVO);

    /**
     * Método utilizado para enviar um processo de movimentação realizado no ProLog para o Globus.
     * <p>
     * O Globus possui um padrão diferente de processo de movimentação. Diferente de como é no ProLog, que possuímos
     * origens e destinos, no Globus tratamos apenas como <code>retiradas</code> e <code>colocações</code>. Assim,
     * temos a seguinte equivalência:
     * 1 - Ao mover um pneu do Veículo para o Estoque, no ProLog, teremos no Globus uma retirada do pneu;
     * 2 - Ao mover um pneu do Estoque para o Veíuclo, no ProLog, teremos no Globus uma colocação;
     * 3 - Ao rotacionar um pneu no Veiculo, no ProLog, teremos no Globus, uma retirada do primeiro pneu, uma retirada
     * do segundo pneu, uma colocação do primeiro pneu (na posição do segundo) e uma colocação do segundo pneu
     * (na posição do primeiro).
     * <p>
     * Para cada rotação no ProLog, teremos 4 movimentações no Globus.
     *
     * @param url {@link retrofit2.http.Url Url} para onde a movimentação será enviada.
     * @param processoMovimentacaoGlobus Informações das movimentações que serão enviadas para o Globus.
     * @return
     * @throws Throwable
     */
    @NotNull
    SuccessResponseIntegracao insertProcessoMovimentacao(
            @NotNull final String url,
            @NotNull final ProcessoMovimentacaoGlobus processoMovimentacaoGlobus) throws Throwable;
}
