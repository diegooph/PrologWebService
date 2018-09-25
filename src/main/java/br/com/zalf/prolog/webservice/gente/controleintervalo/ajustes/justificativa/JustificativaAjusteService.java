package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.justificativa;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 05/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class JustificativaAjusteService {

    private static final String TAG = JustificativaAjusteService.class.getSimpleName();
    @NotNull
    private final JustificativaAjusteDao dao = Injection.provideJustificativaAjusteDao();
    @NotNull
    private final ProLogExceptionHandler exceptionHandler = Injection.provideProLogExceptionHandler();

    AbstractResponse insertJustificativaAjuste(@NotNull final String userToken,
                                               @NotNull final JustificativaAjuste justificativaAjuste) throws ProLogException {
        try {
            return ResponseWithCod.ok("Justificativa inserida com sucesso",
                    dao.insertJustificativaAjuste(TokenCleaner.getOnlyToken(userToken), justificativaAjuste));
        } catch (Throwable e) {
            Log.e(TAG, "Erro ao inserir a Justificativa", e);
            throw exceptionHandler.map(e, "Erro ao inserir a Justificativa, tente novamente");
        }
    }
}
