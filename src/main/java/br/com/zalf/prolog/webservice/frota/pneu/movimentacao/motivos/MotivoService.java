package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoInsercao;
import br.com.zalf.prolog.webservice.geral.unidade.UnidadeService;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-03-18
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public class MotivoService {

    private static final String TAG = UnidadeService.class.getSimpleName();

    @NotNull
    private final MotivoDao dao = Injection.provideMotivoDao();

    @NotNull
    public Long insert(@NotNull final MotivoInsercao motivo) {
        try {
            return dao.insert(motivo);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao inserir motivo %s", motivo.getDescricaoMotivoTroca()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir motivo, tente novamente.");
        }
    }

}
