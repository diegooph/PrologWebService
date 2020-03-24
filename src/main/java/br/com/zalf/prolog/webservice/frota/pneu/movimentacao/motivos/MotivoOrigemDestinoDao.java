package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoOrigemDestinoInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoOrigemDestinoListagemMotivos;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoOrigemDestinoVisualizacaoListagem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2020-03-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public interface MotivoOrigemDestinoDao {

    /**
     * Insere um {@link MotivoOrigemDestinoInsercao motivoOrigemDestino} no banco de dados.
     * <p>
     * Todos os parâmetros são obrigatórios.
     *
     * @param motivoOrigemDestinoInsercao um motivo origem destino a ser inserido.
     * @param tokenAutenticacao           o token de sessão do usuário;
     * @throws Throwable Caso qualquer erro ocorra.
     */
    @NotNull
    Long insert(@NotNull final MotivoOrigemDestinoInsercao motivoOrigemDestinoInsercao,
                @NotNull final String tokenAutenticacao) throws Throwable;

    /**
     * Busca um {@link MotivoOrigemDestinoVisualizacaoListagem motivoOrigemDestino} no banco de dados.
     *
     * @param codMotivoOrigemDestino um código de motivoOrigemDestino a ser buscado no banco.
     * @param tokenAutenticacao      o token de sessão do usuário.
     * @throws Throwable Caso qualquer erro ocorra.
     */
    @NotNull
    MotivoOrigemDestinoVisualizacaoListagem getMotivoOrigemDestino(@NotNull final Long codMotivoOrigemDestino,
                                                                   @NotNull final String tokenAutenticacao) throws Throwable;

    /**
     * Busca uma lista de {@link MotivoOrigemDestinoVisualizacaoListagem motivoOrigemDestino} no banco de dados.
     *
     * @param codEmpresa        um código de empresa a ser usada de parâmetro na consulta ao banco de dados.
     * @param tokenAutenticacao o token de sessão do usuário;
     * @throws Throwable Caso qualquer erro ocorra.
     */
    @NotNull
    List<MotivoOrigemDestinoVisualizacaoListagem> getMotivosOrigemDestino(@NotNull final Long codEmpresa,
                                                                          @NotNull final String tokenAutenticacao) throws Throwable;

    @NotNull
    MotivoOrigemDestinoListagemMotivos getMotivosByOrigemAndDestinoAndUnidade(@NotNull final OrigemDestinoEnum origem,
                                                                              @NotNull final OrigemDestinoEnum destino,
                                                                              @NotNull final Long codUnidade) throws Throwable;

}
