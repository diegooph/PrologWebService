package br.com.zalf.prolog.webservice.cs.nps;

import br.com.zalf.prolog.webservice.cs.nps.model.PesquisaNpsBloqueio;
import br.com.zalf.prolog.webservice.cs.nps.model.PesquisaNpsDisponivel;
import br.com.zalf.prolog.webservice.cs.nps.model.PesquisaNpsRealizada;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created on 2019-10-10
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface PesquisaNpsDao {

    /**
     * Busca uma pesquisa de NPS para realização pelo colaborador, caso uma existir. Uma pesquisa está disponível para
     * realização se passar nas quatro condições seguintes:
     * 1 - Está ativa.
     * 2 - O período de veículação da pesquisa deve abranger a data em que a busca é feita.
     * 3 - O colaborador não deve ter bloqueado a pesquisa.
     * 4 - O colaborador não pode já ter respondido a pesquisa.
     *
     * @param codColaborador O código do colaborador para o qual iremos buscar a pesquisa de NPS.
     * @return Uma pesquisa de NPS para realização.
     * @throws Throwable Caso algum erro ocorrer.
     */
    @NotNull
    Optional<PesquisaNpsDisponivel> getPesquisaNpsColaborador(@NotNull final Long codColaborador) throws Throwable;

    /**
     * Insere as respostas de uma pesquisa de NPS.
     *
     * @param pesquisaRealizada As respostas da pesquisa de NPS que foi realizada.
     * @return O código das respostas da pesquisa de NPS inserida.
     * @throws Throwable Caso algum erro ocorrer.
     */
    @NotNull
    Long insereRespostasPesquisaNps(@NotNull final PesquisaNpsRealizada pesquisaRealizada) throws Throwable;

    /**
     * Bloqueia uma pesquisa de NPS para um colaborador específico. Dessa forma, mesmo sem ter respondido a pesquisa,
     * ela não aparecerá mais para ele.
     *
     * @param pesquisaBloqueio Informações de qual pesquisa de NPS e para qual colaborador iremos realizar o bloqueio.
     * @throws Throwable Caso algum erro ocorrer.
     */
    void bloqueiaPesquisaNpsColaborador(@NotNull final PesquisaNpsBloqueio pesquisaBloqueio) throws Throwable;
}