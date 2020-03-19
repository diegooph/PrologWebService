package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoVisualizacaoListagem;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2020-03-18
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class MotivoService {

    private static final String TAG = MotivoService.class.getSimpleName();

    @NotNull
    private final MotivoDao dao = Injection.provideMotivoDao();

    @NotNull
    public Long insert(@NotNull final MotivoInsercao motivo) {
        try {
            return dao.insert(motivo);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao inserir motivo %s", motivo.getDescricaoMotivo()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir motivo, tente novamente.");
        }
    }

    @NotNull
    public MotivoVisualizacaoListagem getMotivoByCodigo(@NotNull final Long codMotivo) {
        try {
            return dao.getMotivoByCodigo(codMotivo);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar motivo %d", codMotivo), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar motivo, tente novamente.");
        }
    }

    @NotNull
    public List<MotivoVisualizacaoListagem> getMotivosListagem(@NotNull final Long codEmpresa) {
        try {
            return dao.getMotivosListagem(codEmpresa);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar motivos, código da empresa: %d", codEmpresa), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar motivos, tente novamente.");
        }
    }

}
