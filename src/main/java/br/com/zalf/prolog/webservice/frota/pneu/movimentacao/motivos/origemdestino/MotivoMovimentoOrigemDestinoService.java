package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.origemdestino;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
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
public final class MotivoMovimentoOrigemDestinoService {
    @NotNull
    private static final String TAG = MotivoMovimentoOrigemDestinoService.class.getSimpleName();
    @NotNull
    private final MotivoMovimentoOrigemDestinoDao dao = Injection.provideMotivoOrigemDestinoDao();

    public void insert(@NotNull final List<MotivoMovimentoOrigemDestinoInsercao> unidades,
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
    public MotivoMovimentoOrigemDestinoVisualizacao getMotivoOrigemDestino(@NotNull final Long codMotivoOrigemDestino,
                                                                           @NotNull final ZoneId timeZone) {
        try {
            return dao.getMotivoOrigemDestino(codMotivoOrigemDestino, timeZone);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar relação motivo, origem e destino %d", codMotivoOrigemDestino), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar relação motivo, origem e destino, tente novamente.");
        }
    }

    @NotNull
    public List<MotivoMovimentoOrigemDestinoListagem> getMotivosOrigemDestino(@NotNull final Long codColaborador) {
        try {
            return dao.getMotivosOrigemDestino(codColaborador);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar relações motivo, origem e destino.", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar relação motivo, origem e destino, tente novamente.");
        }
    }

    @NotNull
    public MotivoMovimentoOrigemDestinoListagemMotivos getMotivosByOrigemAndDestinoAndUnidade(
            @NotNull final OrigemDestinoEnum origemMovimento,
            @NotNull final OrigemDestinoEnum destinoMovimento,
            @NotNull final Long codUnidade) {
        try {
            return dao.getMotivosByOrigemAndDestinoAndUnidade(origemMovimento, destinoMovimento, codUnidade);
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
    public List<OrigemDestinoListagem> getRotasExistentesByUnidade(@NotNull final Long codUnidade) {
        try {
            return dao.getRotasExistentesByUnidade(codUnidade);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar relações origem e destino, para a a unidade %d", codUnidade), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar origens e destinos, tente novamente.");
        }
    }

}
