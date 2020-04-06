package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.TransicaoExistenteUnidade;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.TransicaoVisualizacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.insercao.MotivoMovimentoTransicaoInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem.TransicaoUnidadeMotivos;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem.UnidadeTransicoesMotivoMovimento;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.util.List;

/**
 * Created on 2020-03-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public interface MotivoMovimentoTransicaoDao {

    /**
     * Insere uma lista de {@link MotivoMovimentoTransicaoInsercao transições} no banco de dados.
     *
     * @param unidades                  uma lista de unidades com suas rotas e seus motivos por rotas.
     * @param codigoColaboradorInsercao codigo do colaborador que está realizando a inserção do registro da relação.
     * @throws Throwable Caso qualquer erro ocorra.
     */
    void insert(@NotNull final List<MotivoMovimentoTransicaoInsercao> unidades,
                @NotNull final Long codigoColaboradorInsercao) throws Throwable;

    /**
     * Busca uma {@link TransicaoVisualizacao transição} no banco de dados.
     *
     * @param codTransicao um código da transição a ser buscada no banco.
     * @param timeZone     o time zone do usuário que está fazendo a requisição.
     * @throws Throwable Caso qualquer erro ocorra.
     */
    @NotNull
    TransicaoVisualizacao getTransicaoVisualizacao(@NotNull final Long codTransicao,
                                                   @NotNull final ZoneId timeZone) throws Throwable;

    /**
     * Busca uma lista de {@link TransicaoVisualizacao transições} no banco de dados
     * com base nas unidades que o usuário tem permissão de editar, buscado através do token do usuário.
     *
     * @param codColaborador código do colaborador que está realizando a requisição, para identificar as unidades
     *                       liberadas para ele.
     * @throws Throwable Caso qualquer erro ocorra.
     */
    @NotNull
    List<UnidadeTransicoesMotivoMovimento> getUnidadesTransicoesMotivoMovimento(@NotNull final Long codColaborador)
            throws Throwable;

    /**
     * Busca uma lista de {@link TransicaoUnidadeMotivos transições} no banco de dados com base em uma origem,
     * destino e unidade.
     *
     * @param origemMovimento  a origem a ser utilizada de filtro.
     * @param destinoMovimento o destino a ser usado de filtro.
     * @param codUnidade       o código da unidade dos motivos que estão sendo buscados.
     * @throws Throwable Caso qualquer erro ocorra.
     */
    @NotNull
    TransicaoUnidadeMotivos getMotivosTransicaoUnidade(
            @NotNull final OrigemDestinoEnum origemMovimento,
            @NotNull final OrigemDestinoEnum destinoMovimento,
            @NotNull final Long codUnidade) throws Throwable;

    /**
     * Busca uma lista de {@link TransicaoExistenteUnidade transições} no banco de dados com base em uma origem,
     * destino e unidade.
     *
     * @param codUnidade o código da unidade das relações de origem e destino que serão buscadas.
     * @throws Throwable Caso qualquer erro ocorra.
     */
    @NotNull
    List<TransicaoExistenteUnidade> getTransicoesExistentesByUnidade(@NotNull final Long codUnidade) throws Throwable;

}
