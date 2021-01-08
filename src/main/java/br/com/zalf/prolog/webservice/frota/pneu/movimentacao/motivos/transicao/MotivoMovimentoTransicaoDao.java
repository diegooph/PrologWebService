package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.TransicaoExistenteUnidade;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.TransicaoVisualizacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.insercao.MotivoMovimentoTransicaoInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem.TransicaoUnidadeMotivos;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem.UnidadeTransicoesMotivoMovimento;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.time.ZoneId;
import java.util.List;

/**
 * Created on 2020-03-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public interface MotivoMovimentoTransicaoDao {

    void insert(@NotNull final List<MotivoMovimentoTransicaoInsercao> unidades,
                @NotNull final Long codigoColaboradorInsercao) throws Throwable;

    void delete(@NotNull final Long codEmpresa, @NotNull final Connection conn) throws Throwable;

    @NotNull
    TransicaoVisualizacao getTransicaoVisualizacao(@NotNull final Long codTransicao,
                                                   @NotNull final ZoneId timeZone) throws Throwable;

    @NotNull
    List<UnidadeTransicoesMotivoMovimento> getUnidadesTransicoesMotivoMovimento(@NotNull final Long codColaborador)
            throws Throwable;

    @NotNull
    TransicaoUnidadeMotivos getMotivosTransicaoUnidade(
            @NotNull final OrigemDestinoEnum origemMovimento,
            @NotNull final OrigemDestinoEnum destinoMovimento,
            @NotNull final Long codUnidade) throws Throwable;

    @NotNull
    List<TransicaoExistenteUnidade> getTransicoesExistentesByUnidade(@NotNull final Long codUnidade) throws Throwable;

}
