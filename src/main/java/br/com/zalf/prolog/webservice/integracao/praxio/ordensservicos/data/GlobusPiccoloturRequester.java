package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.data;

import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.OrdemDeServicoCorretivaPrologVO;
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
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Long insertItensNok(
            @NotNull final OrdemDeServicoCorretivaPrologVO ordemDeServicoCorretivaPrologVO) throws Throwable;
}
