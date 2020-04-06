package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
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
public final class MotivoMovimentoTransicaoService {
    @NotNull
    private static final String TAG = MotivoMovimentoTransicaoService.class.getSimpleName();
    @NotNull
    private final MotivoMovimentoTransicaoDao dao = Injection.provideMotivoOrigemDestinoDao();

    public void insert(@NotNull final List<MotivoMovimentoTransicaoInsercao> unidades,
                       @NotNull final Long codigoColaboradorInsercao) {
        try {
            dao.insert(unidades, codigoColaboradorInsercao);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir uma lista de relação motivo, origem e destino.", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar relação motivo, origem e destino, tente novamente.");
        }
    }

    @NotNull
    public TransicaoVisualizacao getTransicaoVisualizacao(@NotNull final Long codTransicao,
                                                          @NotNull final ZoneId timeZone) {
        try {
            return dao.getTransicaoVisualizacao(codTransicao, timeZone);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar relação motivo, origem e destino %d", codTransicao), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar relação motivo, origem e destino, tente novamente.");
        }
    }

    @NotNull
    public List<UnidadeTransicoesMotivoMovimento> getUnidadesTransicoesMotivoMovimento(
            @NotNull final Long codColaborador) {
        try {
            return dao.getUnidadesTransicoesMotivoMovimento(codColaborador);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar relações motivo, origem e destino.", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar relação motivo, origem e destino, tente novamente.");
        }
    }

    @NotNull
    public TransicaoUnidadeMotivos getMotivosTransicaoUnidade(
            @NotNull final OrigemDestinoEnum origemMovimento,
            @NotNull final OrigemDestinoEnum destinoMovimento,
            @NotNull final Long codUnidade) {
        try {
            return dao.getMotivosTransicaoUnidade(origemMovimento, destinoMovimento, codUnidade);
        } catch (final Throwable t) {
            Log.e(TAG, String.format(
                    "Erro ao buscar relação motivo, origem e destino, para a origem %s e destino %s",
                    origemMovimento.toString(),
                    destinoMovimento.toString()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar motivos, tente novamente.");
        }
    }

    @NotNull
    public List<TransicaoExistenteUnidade> getTransicoesExistentesByUnidade(@NotNull final Long codUnidade) {
        try {
            return dao.getTransicoesExistentesByUnidade(codUnidade);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar relações origem e destino, para a a unidade %d", codUnidade), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar origens e destinos, tente novamente.");
        }
    }

}
