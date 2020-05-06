package br.com.zalf.prolog.webservice.integracao.api.afericao;

import br.com.zalf.prolog.webservice.integracao.api.afericao._model.AfericaoRealizada;

import java.util.List;

/**
 * Created on 2020-05-06
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
interface ApiAfericaoDao {

    /**
     * Método responsável por buscar todas as aferições realizadas a partir do {@code codUltimaAfericao} recebido.
     * O códifo da Última Aferição Sincronizada é utilizado como um Offset de busca, todas as aferições a partir
     * deste código serão retornadas por este método.
     *
     * @param tokenIntegracao   Token utilizado para a requisição. Este token será utilizado para
     *                          descobrir qual empresa está requisitando as informações.
     * @param codUltimaAfericao Código da Última aferição sincronizada.
     * @return Uma lista de {@link List<AfericaoRealizada> aferições} não sincronizadas.
     * @throws Throwable Se algum erro ocorrer durante a busca das novas aferições.
     */
    List<AfericaoRealizada> getAfericoesRealizadas(String tokenIntegracao, Long codUltimaAfericao) throws Throwable;
}
