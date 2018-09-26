package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAdicao;
import br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.MarcacaoAjusteAdicaoInicioFim;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ControleJornadaAjusteService {

    private static final String TAG = ControleJornadaAjusteService.class.getSimpleName();
    @NotNull
    private final ControleJornadaAjusteDao dao = Injection.provideControleJornadaAjustesDao();
    @NotNull
    private final ProLogExceptionHandler exceptionHandler = Injection.provideProLogExceptionHandler();

    public Response adicionarMarcacaoAjuste(@NotNull final String userToken,
                                            @NotNull final MarcacaoAjusteAdicao marcacaoAjuste) throws ProLogException {
        try {
            dao.adicionarMarcacaoAjuste(TokenCleaner.getOnlyToken(userToken), marcacaoAjuste);
            return Response.ok("Adição de marcação realizada");
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao realizar adição de marcação", e);
            throw exceptionHandler.map(e, "Erro ao realizar adição de marcação, tente novamente");
        }
    }

    public Response adicionarMarcacaoAjusteInicioFim(
            @NotNull final String userToken,
            @NotNull final MarcacaoAjusteAdicaoInicioFim marcacaoAjuste) throws ProLogException {
        try {
            dao.adicionarMarcacaoAjusteInicioFim(TokenCleaner.getOnlyToken(userToken), marcacaoAjuste);
            return Response.ok("Adição das marcações de início e fim realizadas");
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao realizar adição das marcações de início e fim", e);
            throw exceptionHandler.map(
                    e,
                    "Erro ao realizar adição das marcações de início e fim, tente novamente");
        }
    }
}
