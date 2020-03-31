package br.com.zalf.prolog.webservice.integracao.praxio.data;

import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.GlobusPiccoloturLocalMovimento;
import br.com.zalf.prolog.webservice.integracao.praxio.movimentacao.ProcessoMovimentacaoGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.soap.OrdemDeServicoCorretivaPrologVO;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
     * Método utilizado para autenticar a requisição na api sendo consumida.
     * <p>
     * Para autenticar a requisição utilizamos um {@code token} e um {@code shortCode}. Validando essas informações
     * obteremos um <code>Token</code> para utilizar no header das requisições.
     *
     * @param url       <code>URL</code> onde iremos conectar para obter o token.
     * @param token     String contendo letras e números que serão utilizados para autenticar.
     * @param shortCode Número inteiro que será utilizado em conjunto com o {@code token} para autenticar.
     * @return Objeto contendo o <code>Token</code> para utilizar no header das demais requisições.
     * @throws Throwable Se algum erro acontecer.
     */
    @NotNull
    GlobusPiccoloturAutenticacaoResponse getTokenAutenticacaoIntegracao(
            @NotNull final String url,
            @NotNull final String token,
            @NotNull final Long shortCode) throws Throwable;

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
     * @param url                        {@link retrofit2.http.Url Url} para onde a movimentação será enviada.
     * @param tokenIntegracao            Token obtido pelo método {@code getTokenAutenticacaoIntegracao}.
     * @param processoMovimentacaoGlobus Informações das movimentações que serão enviadas para o Globus.
     * @return {@link GlobusPiccoloturMovimentacaoResponse Resposta} do globus indicando se a operação foi sucesso ou
     * erro.
     * @throws Throwable Caso algum erro aconteça.
     * @see GlobusPiccoloturRequester#getTokenAutenticacaoIntegracao(String, String, Long).
     */
    @NotNull
    GlobusPiccoloturMovimentacaoResponse insertProcessoMovimentacao(
            @NotNull final String url,
            @NotNull final String tokenIntegracao,
            @NotNull final ProcessoMovimentacaoGlobus processoMovimentacaoGlobus) throws Throwable;

    /**
     * Método utilizado para buscar do Globus os Locais de movimento que o colaborador tem acesso. Utilizamos o
     * {@code cpfColaborador} para filtrar apenas os locais liberados para o colaborador em questão.
     * <p>
     * O Globus possui o conceito de Local de Movimento. Trata-se da unidade onde a movimentação está sendo realizada,
     * uma vez que pode-se fazer movimentação de pneus em veículo em diferentes unidades, não necessáriamente na
     * unidede onde o veículo foi cadastrado.
     *
     * A Unidade de Movimento define em qual unidade o pneu retirado do veículo será colocado no estoque.
     *
     * @param url             {@link retrofit2.http.Url Url} de onde os recursos serão buscados.
     * @param tokenIntegracao Token obtido pelo método {@code getTokenAutenticacaoIntegracao}.
     * @param cpfColaborador  Cpf do colaborador. Será utilizado para buscar os locais do Globus a qual ele tem acesso.
     * @return A lista de {@link GlobusPiccoloturLocalMovimento locais Globus} que o colaborador tem acesso no Globus.
     * @throws Throwable Caso algum erro aconteça.
     * @see GlobusPiccoloturRequester#getTokenAutenticacaoIntegracao(String, String, Long).
     */
    @NotNull
    List<GlobusPiccoloturLocalMovimento> getLocaisMovimentoGlobus(
            @NotNull final String url,
            @NotNull final String tokenIntegracao,
            @NotNull final String cpfColaborador) throws Throwable;
}
