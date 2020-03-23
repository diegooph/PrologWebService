package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoOrigemDestinoInsercao;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-03-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public class MotivoOrigemDestinoService {

    @NotNull
    private static final String TAG = MotivoOrigemDestinoService.class.getSimpleName();

    @NotNull
    private final MotivoOrigemDestinoDao dao = Injection.provideMotivoOrigemDestinoDao();

    @NotNull
    public Long insert(@NotNull final MotivoOrigemDestinoInsercao motivoOrigemDestinoInsercao, @NotNull final String tokenAutenticacao) {
        try {
            return dao.insert(motivoOrigemDestinoInsercao, tokenAutenticacao);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao inserir relação motivo, origem e destino %s", motivoOrigemDestinoInsercao.getCodMotivo()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir motivo, tente novamente.");
        }
    }

}
