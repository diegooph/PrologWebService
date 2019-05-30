package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.integracao.praxio.afericao.MedicaoIntegracaoPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.ItemOSAbertaGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.ItemResolvidoGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.OrdemServicoAbertaGlobus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 12/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
interface IntegracaoPraxioDao {

    /**
     * Este método busca as {@link MedicaoIntegracaoPraxio aferições} a partir
     * do {@code codUltimaAfericao} recebido.
     * O código da Última Aferição Sincronizada é utilizado como um Offset de busca,
     * todas as aferições a partir deste código serão retornadas por este método.
     *
     * @param tokenIntegracao   Token utilizado para a requisição. Este token será utilizado para
     *                          descobrir qual empresa está requisitando as informações.
     * @param codUltimaAfericao Código da Última aferição sincronizada.
     * @return Uma lista de {@link List <MedicaoIntegracaoPraxio> aferições} não sincronizadas.
     * @throws Throwable Se algum erro ocorrer durante a busca das novas aferições.
     */
    @NotNull
    List<MedicaoIntegracaoPraxio> getAfericoesRealizadas(@NotNull final String tokenIntegracao,
                                                         @NotNull final Long codUltimaAfericao) throws Throwable;

    /**
     * Método utilizado para inserir uma série de {@link OrdemServicoAbertaGlobus Ordens de Serviço Abertas} do Sistema
     * Globus no ProLog.
     * <p>
     * Esse método deverár receber apenas Ordens de Serviços Abertas e apenas
     * {@link ItemOSAbertaGlobus Itens de O.S Pendentes}. Itens já resolvidos não devem ser enviados nesta lista.
     * <p>
     * Caso a Ordem de Serviço já exista, não será criada uma nova. Os itens presentes na Segunda O.S (O.S recebida)
     * serão adicionados na O.S que já existe, desde que esta O.S não esteja finalizada.
     *
     * @param tokenIntegracao      Token utilizado para autenticar o Sistema que deseja realizar a operação.
     * @param ordensServicoAbertas Lista de informações que serão inseridas no Banco de Dados.
     * @throws Throwable Se algum erro ocorrer no processamento das informações.
     */
    void inserirOrdensServicoGlobus(@NotNull final String tokenIntegracao,
                                    @NotNull final List<OrdemServicoAbertaGlobus> ordensServicoAbertas) throws Throwable;

    void resolverMultiplosItens(@NotNull final String tokenIntegracao,
                                @NotNull final List<ItemResolvidoGlobus> itensResolvidos) throws Throwable;
}