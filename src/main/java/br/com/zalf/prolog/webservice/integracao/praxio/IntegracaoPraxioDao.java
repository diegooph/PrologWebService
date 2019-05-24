package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.integracao.praxio.afericao.MedicaoIntegracaoPraxio;
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

    void inserirOrdensServicoGlobus(@NotNull final String tokenIntegracao,
                                    @NotNull final List<OrdemServicoAbertaGlobus> ordensServicoAbertas) throws Throwable;
}