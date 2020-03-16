package br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.andamento.ViagemEmAndamento;
import br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento.descanso.ViagemEmDescanso;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 31/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface AcompanhamentoViagemDao {

    /**
     * Este método busca todos os colaboradores que estão em descanso. É entendido como 'em descanso' o colaborador que
     * não tem nenhuma marcação do tipo jornada em aberto. Ou seja, a última marcação do tipo jornada desse colaborador
     * deve ter, pelo menos, o fim. Podendo inclusive ser um fim sem início.
     *
     * @param codUnidade Código da {@link Unidade unidade} para a qual as informações serão buscadas.
     * @param codCargos  Código dos {@link Cargo cargos} para os quais as informações serão buscadas.
     * @return Um objeto contendo todos os colaboradores que estão em descanso.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    @NotNull
    ViagemEmDescanso getColaboradoresEmDescanso(@NotNull final Long codUnidade,
                                                @NotNull final List<Long> codCargos) throws Throwable;

    /**
     * Este método busca todas as viagens que estão em andamento bem como informações dos colaboradores que estão
     * realizando essas viagens. É entendido como tendo viagem 'em andamento' o colaborador cuja última marcação do tipo
     * jornada é um início que não possui fim.
     *
     * <b>Pontos importantes:</b>
     * 1 - Inícios que possuam fins inativos não serão considerados como viagens em andamento.
     * 2 - Se um colaborador tem uma marcação de início de tipo jornada sem fim, e depois dessa uma jornada completa com
     * início e fim, caso essa jornada completa tenha seu início e fim inativados, o início avulso antes dela não
     * passará a ser considerado uma viagem em andamento.
     *
     * @param codUnidade Código da {@link Unidade unidade} para a qual as informações serão buscadas.
     * @param codCargos  Código dos {@link Cargo cargos} para os quais as informações serão buscadas.
     * @return Um objeto contendo todas as viagens em andamento.
     * @throws Throwable Caso qualquer erro aconteça.
     */
    @NotNull
    ViagemEmAndamento getViagensEmAndamento(@NotNull final Long codUnidade,
                                            @NotNull final List<Long> codCargos) throws Throwable;

    /**
     * Método utilizado para buscar uma {@link MarcacaoAgrupadaAcompanhamento marcação agrupada}. Para realizar a busca
     * pode-se utilizar o código de início, fim ou ambos os código da marcação. Caso a marcação não possua vínculo,
     * ou seja, for uma marcação de início que não possui fim ou uma marcação de fim que não possui início,
     * o objeto retornado conterá apenas as informações da marcação avulsa.
     *
     * @param codUnidade Código da {@link Unidade unidade} onde a marcação foi realizada.
     * @param codInicio  Código de Início da marcação.
     * @param codFim     Código de Fim da marcação.
     * @return {@link MarcacaoAgrupadaAcompanhamento marcação agrupada} contendo as informações da(s)
     * marcação(ões) buscada(s).
     * @throws Throwable Caso algum erro aconteça na busca das informações
     */
    @NotNull
    MarcacaoAgrupadaAcompanhamento getMarcacaoInicioFim(@NotNull final Long codUnidade,
                                                        @Nullable final Long codInicio,
                                                        @Nullable final Long codFim) throws Throwable;
}