package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoRetiradaOrigemDestinoInsercao;
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
public class MotivoRetiradaOrigemDestinoService {

    @NotNull
    private static final String TAG = MotivoRetiradaOrigemDestinoService.class.getSimpleName();

    @NotNull
    private final MotivoRetiradaOrigemDestinoDao dao = Injection.provideMotivoOrigemDestinoDao();

    @NotNull
    public Long insert(@NotNull final MotivoRetiradaOrigemDestinoInsercao motivoRetiradaOrigemDestinoInsercao,
                       @NotNull final String tokenAutenticacao) {
        try {
            return dao.insert(motivoRetiradaOrigemDestinoInsercao, tokenAutenticacao);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao inserir relação motivo, origem e destino %d", motivoRetiradaOrigemDestinoInsercao.getCodMotivoRetirada()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir motivo, tente novamente.");
        }
    }

    @NotNull
    public List<Long> insert(@NotNull final List<MotivoRetiradaOrigemDestinoInsercaoBatch> origensDestinosMotivos,
                             @NotNull final String tokenAutenticacao) {
        try {
            return dao.insert(origensDestinosMotivos, tokenAutenticacao);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir uma lista de relação motivo, origem e destino.", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar relação motivo, origem e destino, tente novamente.");
        }
    }

    @NotNull
    public MotivoRetiradaOrigemDestinoVisualizacaoListagem getMotivoOrigemDestino(@NotNull final Long codMotivoOrigemDestino,
                                                                                  @NotNull final String tokenAutenticacao) {
        try {
            return dao.getMotivoOrigemDestino(codMotivoOrigemDestino, tokenAutenticacao);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar relação motivo, origem e destino %d", codMotivoOrigemDestino), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar relação motivo, origem e destino, tente novamente.");
        }
    }

    @NotNull
    public List<MotivoRetiradaOrigemDestinoVisualizacaoListagem> getMotivosOrigemDestino(@NotNull final Long codEmpresa,
                                                                                         @NotNull final String tokenAutenticacao) {
        try {
            return dao.getMotivosOrigemDestino(codEmpresa, tokenAutenticacao);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar relação motivo, origem e destino, para a empresa de código %d", codEmpresa), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar relação motivo, origem e destino, tente novamente.");
        }
    }

    @NotNull
    public MotivoRetiradaOrigemDestinoListagemMotivos getMotivosByOrigemAndDestinoAndUnidade(@NotNull final OrigemDestinoEnum origem,
                                                                                             @NotNull final OrigemDestinoEnum destino,
                                                                                             @NotNull final Long codUnidade) {
        try {
            return dao.getMotivosByOrigemAndDestinoAndUnidade(origem, destino, codUnidade);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar relação motivo, origem e destino, para a origem %s e destino %s", origem.toString(), destino.toString()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar motivos, tente novamente.");
        }
    }

}
