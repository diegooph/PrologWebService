package br.com.zalf.prolog.webservice.integracao.praxio;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 12/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
interface IntegracaoPraxioDao {

    /**
     * Este método busca as {@link AfericaoIntegracaoPraxio aferições} a partir
     * do {@code codUltimaAfericao} recebido.
     * O código da Última Aferição Sincronizada é utilizado como um Offset de busca,
     * todas as aferições a partir deste código serão retornadas por este método.
     *
     * @param codUltimaAfericao Código da Última aferição sincronizada.
     * @return Uma lista de {@link List <AfericaoIntegracaoPraxio> aferições} não sincronizadas.
     * @throws Throwable Se algum erro ocorrer durante a busca das novas aferições.
     */
    @NotNull
    List<AfericaoIntegracaoPraxio> getAfericoesRealizadas(@NotNull final Long codUltimaAfericao) throws Throwable;

    /**
     * Método utilizado para verificar se o token recebido é válido.
     * Para um token ser considerado válido é necessário que ele exista na tabela
     * de Tokens de Empresas integradas, no Banco de Dados do ProLog.
     *
     * @param tokenIntegracao Token recebido na requisição, que será validado.
     * @return <code>TRUE</code> se o token for válido, <code>FALSE</code> caso contrário.
     * @throws Throwable Se algum erro ocorrer durante a verificação do Token.
     */
    boolean verifyIfTokenIntegracaoExists(@NotNull final String tokenIntegracao) throws Throwable;
}
