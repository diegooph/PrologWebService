package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.origemdestino;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.OrigemDestinoListagem;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.origemdestino._model.MotivoMovimentoOrigemDestinoInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.origemdestino._model.MotivoMovimentoOrigemDestinoListagem;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.origemdestino._model.MotivoMovimentoOrigemDestinoListagemMotivos;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.origemdestino._model.MotivoMovimentoOrigemDestinoVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.util.List;

/**
 * Created on 2020-03-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public interface MotivoMovimentoOrigemDestinoDao {

    /**
     * Insere uma lista de {@link MotivoMovimentoOrigemDestinoInsercao origemDestinoMotivo} no banco de dados.
     *
     * @param unidades                  uma lista de unidades com suas rotas e seus motivos por rotas.
     * @param codigoColaboradorInsercao codigo do colaborador que está realizando a inserção do registro da relação.
     * @throws Throwable Caso qualquer erro ocorra.
     */
    void insert(@NotNull final List<MotivoMovimentoOrigemDestinoInsercao> unidades,
                @NotNull final Long codigoColaboradorInsercao) throws Throwable;

    /**
     * Busca um {@link MotivoMovimentoOrigemDestinoVisualizacao motivoOrigemDestino} no banco de dados.
     *
     * @param codMotivoOrigemDestino um código de motivoOrigemDestino a ser buscado no banco.
     * @param timeZone               o timezone do usuário que está fazendo a requisição.
     * @throws Throwable Caso qualquer erro ocorra.
     */
    @NotNull
    MotivoMovimentoOrigemDestinoVisualizacao getMotivoOrigemDestino(@NotNull final Long codMotivoOrigemDestino,
                                                                    @NotNull final ZoneId timeZone) throws Throwable;

    /**
     * Busca uma lista de {@link MotivoMovimentoOrigemDestinoVisualizacao motivoOrigemDestino} no banco de dados
     * com base nas unidades que o usuário tem permissão de editar, buscado através do token do usuário.
     *
     * @param codColaborador código do colaborador que está realizando a requisição, para identificar as unidades
     *                       liberadas para ele.
     * @throws Throwable Caso qualquer erro ocorra.
     */
    @NotNull
    List<MotivoMovimentoOrigemDestinoListagem> getMotivosOrigemDestino(@NotNull final Long codColaborador)
            throws Throwable;

    /**
     * Busca uma lista de {@link MotivoMovimentoOrigemDestinoListagemMotivos motivoOrigemDestino} no banco de dados
     * com base em uma origem, destino e unidade.
     *
     * @param origemMovimento  a origem a ser utilizada de filtro.
     * @param destinoMovimento o destino a ser usado de filtro.
     * @param codUnidade       o código da unidade dos motivos que estão sendo buscados.
     * @throws Throwable Caso qualquer erro ocorra.
     */
    @NotNull
    MotivoMovimentoOrigemDestinoListagemMotivos getMotivosByOrigemAndDestinoAndUnidade(
            @NotNull final OrigemDestinoEnum origemMovimento,
            @NotNull final OrigemDestinoEnum destinoMovimento,
            @NotNull final Long codUnidade) throws Throwable;

    /**
     * Busca uma lista de {@link OrigemDestinoListagem origemDestino} no banco de dados
     * com base em uma origem, destino e unidade.
     *
     * @param codUnidade o código da unidade das relações de origem e destino que serão buscadas.
     * @throws Throwable Caso qualquer erro ocorra.
     */
    @NotNull
    List<OrigemDestinoListagem> getTransicoesExistentesByUnidade(@NotNull final Long codUnidade) throws Throwable;

}
