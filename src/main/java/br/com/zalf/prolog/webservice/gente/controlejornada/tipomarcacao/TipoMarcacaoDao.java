package br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao;

import br.com.zalf.prolog.webservice.gente.unidade._model.Unidade;
import br.com.zalf.prolog.webservice.gente.controlejornada.DadosIntervaloChangedListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 20/12/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface TipoMarcacaoDao {

    /**
     * Método utilizado para salvar um novo {@link TipoMarcacao tipo de marcação}.
     *
     * @param tipoMarcacao      {@link TipoMarcacao Objeto} contendo as informações a serem salvas no banco de dados.
     * @param intervaloListener {@link DadosIntervaloChangedListener Listener} utilizado para executar ações quando certas
     *                          modificações no controle de jornada acontecem.
     * @return O código do {@link TipoMarcacao tipo de marcação} inserido no banco de dados.
     * @throws Throwable Se qualquer erro ocorrer.
     */
    @NotNull
    Long insertTipoMarcacao(@NotNull final TipoMarcacao tipoMarcacao,
                            @NotNull final DadosIntervaloChangedListener intervaloListener) throws Throwable;

    /**
     * Método utilizado para atualizar informações de um {@link TipoMarcacao tipo de marcação}.
     * Se o objetivo é apenas ativar/inativar um {@link TipoMarcacao tipo de marcação} pode ser
     * utilizado o método {@link #updateStatusAtivoTipoMarcacao(Long, TipoMarcacao, DadosIntervaloChangedListener)}.
     *
     * @param tipoMarcacao      {@link TipoMarcacao Objeto} contendo as informações a serem atualizadas.
     * @param intervaloListener {@link DadosIntervaloChangedListener Listener} utilizado para executar ações quando certas
     *                          modificações no controle de jornada acontecem.
     * @throws Throwable Se qualquer erro ocorrer.
     * @see #updateStatusAtivoTipoMarcacao(Long, TipoMarcacao, DadosIntervaloChangedListener).
     */
    void updateTipoMarcacao(@NotNull final TipoMarcacao tipoMarcacao,
                            @NotNull final DadosIntervaloChangedListener intervaloListener) throws Throwable;

    /**
     * Método utilizado para listar os {@link List<TipoMarcacao> tipos de marcações} para o
     * {@code codUnidade} fornecido. Este método recebe como parâmetro também a propriedade
     * {@code apenasAtivos} indicando se na listagem também estarão os tipos de marcações
     * desativados e a propriedade {@code withCargos} para saber se no tipo de marcação estará
     * as informações de quais cargos estão aptos a realizar esse tipo de marcação.
     *
     * @param codUnidade   Código da {@link Unidade Unidade} que serão buscados os tipos de marcações.
     * @param apenasAtivos Booleano indicando se a listagem terá apenas os tipos de marcações ativos
     *                     ou todos os tipos de marcações.
     * @param withCargos   Booleando indicando se os tipos de marcações buscados terão as informações dos
     *                     cargos que são aptos a realizar a marcação de cada intervalo.
     * @return Uma lista de {@link List<TipoMarcacao> tipos de marcações} seguindo
     * @throws Throwable Se qualquer erro ocorrer.
     */
    @NotNull
    List<TipoMarcacao> getTiposMarcacoes(@NotNull final Long codUnidade,
                                         final boolean apenasAtivos,
                                         final boolean withCargos) throws Throwable;

    /**
     * Método utilizado para buscar um {@link TipoMarcacao tipo marcação} específico através
     * do {@code codTipoMarcacao} do tipo de marcação.
     *
     * @param codTipoMarcacao Codigo do {@link TipoMarcacao tipo marcação} que será buscado.
     * @return Um objeto {@link TipoMarcacao tipo marcação} contendo todas as informações.
     * @throws Throwable Se qualquer erro ocorrer.
     */
    @NotNull
    TipoMarcacao getTipoMarcacao(@NotNull final Long codTipoMarcacao) throws Throwable;

    /**
     * Método utilizado para atualizar apenas o status {@link TipoMarcacao#ativo} do
     * {@link TipoMarcacao tipo marcação}.
     * Se for necessário atualizar outras informações de um tipo de marcação pode ser
     * utilizado o método {@link #updateTipoMarcacao(TipoMarcacao, DadosIntervaloChangedListener)}.
     *
     * @param codTipoMarcacao   Código do tipo de marcação que será atualizado.
     * @param tipoMarcacao      Objeto {@link TipoMarcacao tipo de marcação} contendo o
     *                          status {@link TipoMarcacao#ativo}.
     * @param intervaloListener {@link DadosIntervaloChangedListener Listener} utilizado para executar ações quando certas
     *                          modificações no controle de jornada acontecem.
     * @throws Throwable Se qualquer erro ocorrer.
     * @see #updateTipoMarcacao(TipoMarcacao, DadosIntervaloChangedListener)
     */
    void updateStatusAtivoTipoMarcacao(@NotNull final Long codTipoMarcacao,
                                       @NotNull final TipoMarcacao tipoMarcacao,
                                       @NotNull final DadosIntervaloChangedListener intervaloListener) throws Throwable;

    /**
     * A {@link FormulaCalculoJornada fórmula de cálculo de Jornada} é um objeto que contém as informações para a
     * realização do cálculo de Jornada Bruta e da Jornada Líquida.
     * <p>
     * A Jornada Bruta é calculada através da duração total da jornada (diferença de tempo entre início e fim)
     * descontando as {@link FormulaCalculoJornada#tiposDescontadosJornadaBruta marcações} que foram selecionadas
     * pelo usuário para descontar do período de Jornada.
     * <b>Ex: Jornada Bruta = Tempo Total Jornada - Refeição - Descarga</b>
     * <p>
     * A Jornada Líquida, por sua vez, é a diferença entre a Jornada Bruta e as marcações que foram selecionadas pelo
     * usuário para descontar da jornada líquida.
     * As marcações selecionadas para descontar da Jornada Bruta não podem estar marcados para descontar da
     * Jornada Líquida, e o contrário também é válido.
     * <b>Ex: Jornada Líquida = Jornada Bruta - Descanso</b>
     *
     * @param codUnidade Código da {@link Unidade unidade} que será buscado a fórmula de cálculo de jornada.
     * @return A {@link FormulaCalculoJornada fórmula de cálculo de Jornada} para a unidade selecionada.
     * @throws Throwable Se qualquer erro ocorrer.
     */
    @NotNull
    FormulaCalculoJornada getForumaCalculoJornada(@NotNull final Long codUnidade) throws Throwable;

    /**
     * Método para verificar se uma unidade possui algum tipo definido como jornada. Alguns locais do sistema assumem
     * a existência desse tipo definido para gerar algumas informações, se a unidade não tiver, pode ser que essa parte
     * do sistema quebre. Com esse método, é possível validar isso antes de exibir algo que dependa dessa definição.
     *
     * @param codUnidade Código da {@link Unidade unidade} que será verificado se tem tipo definido.
     * @return <code>TRUE</code> se a unidade tiver tipo definido como jornada; <code>FALSE</code> caso contrário.
     * @throws Throwable Se qualquer erro ocorrer
     */
    boolean unidadeTemTipoDefinidoComoJornada(@NotNull final Long codUnidade) throws Throwable;
}