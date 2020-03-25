package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoRetiradaOrigemDestinoInsercaoBatch;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoRetiradaOrigemDestinoListagemMotivos;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoRetiradaOrigemDestinoVisualizacaoListagem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2020-03-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public interface MotivoRetiradaOrigemDestinoDao {

    @NotNull
    List<Long> insert(@NotNull final List<MotivoRetiradaOrigemDestinoInsercaoBatch> origensDestinosMotivos,
                      @NotNull final String tokenAutenticacao) throws Throwable;

    /**
     * Busca um {@link MotivoRetiradaOrigemDestinoVisualizacaoListagem motivoOrigemDestino} no banco de dados.
     *
     * @param codMotivoOrigemDestino um código de motivoOrigemDestino a ser buscado no banco.
     * @param tokenAutenticacao      o token de sessão do usuário.
     * @throws Throwable Caso qualquer erro ocorra.
     */
    @NotNull
    MotivoRetiradaOrigemDestinoVisualizacaoListagem getMotivoOrigemDestino(@NotNull final Long codMotivoOrigemDestino,
                                                                           @NotNull final String tokenAutenticacao) throws Throwable;

    /**
     * Busca uma lista de {@link MotivoRetiradaOrigemDestinoVisualizacaoListagem motivoOrigemDestino} no banco de dados.
     *
     * @param codEmpresa        um código de empresa a ser usada de parâmetro na consulta ao banco de dados.
     * @param tokenAutenticacao o token de sessão do usuário;
     * @throws Throwable Caso qualquer erro ocorra.
     */
    @NotNull
    List<MotivoRetiradaOrigemDestinoVisualizacaoListagem> getMotivosOrigemDestino(@NotNull final Long codEmpresa,
                                                                                  @NotNull final String tokenAutenticacao) throws Throwable;

    /**
     * Busca uma lista de {@link MotivoRetiradaOrigemDestinoVisualizacaoListagem motivoOrigemDestino} no banco de dados.
     *
     * @param origem     a origem a ser utilizada de filtro.
     * @param destino    o destino a ser usado de filtro.
     * @param codUnidade o código da unidade dos motivos que estão sendo buscados.
     * @throws Throwable Caso qualquer erro ocorra.
     */
    @NotNull
    MotivoRetiradaOrigemDestinoListagemMotivos getMotivosByOrigemAndDestinoAndUnidade(@NotNull final OrigemDestinoEnum origem,
                                                                                      @NotNull final OrigemDestinoEnum destino,
                                                                                      @NotNull final Long codUnidade) throws Throwable;

}
