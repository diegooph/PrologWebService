package br.com.zalf.prolog.webservice.integracao.praxio.data;

import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.GlobusPiccoloturLocalMovimentoResponse;
import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.ProcessoMovimentacaoGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.OrdemServicoHolderDto;
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
     * @param ordemServico Objeto que encapsula as informações para enviar ao endpoint do Globus.
     * @return O código da Ordem de Serviço que foi criada no Globus.
     */
    @NotNull
    Long insertItensNok(@NotNull final OrdemServicoHolderDto ordemServico) throws Throwable;

    @NotNull
    GlobusPiccoloturAutenticacaoResponse getTokenAutenticacaoIntegracao(
            @NotNull final ApiAutenticacaoHolder autenticacaoHolder) throws Throwable;

    @NotNull
    GlobusPiccoloturMovimentacaoResponse insertProcessoMovimentacao(
            @NotNull final ApiAutenticacaoHolder autenticacaoHolder,
            @NotNull final String tokenIntegracao,
            @NotNull final ProcessoMovimentacaoGlobus processoMovimentacaoGlobus) throws Throwable;

    @NotNull
    GlobusPiccoloturLocalMovimentoResponse getLocaisMovimentoGlobusResponse(
            @NotNull final ApiAutenticacaoHolder autenticacaoHolder,
            @NotNull final String tokenIntegracao,
            @NotNull final String cpfColaborador) throws Throwable;
}
