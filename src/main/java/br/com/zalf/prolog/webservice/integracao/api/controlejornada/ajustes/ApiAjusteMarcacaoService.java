package br.com.zalf.prolog.webservice.integracao.api.controlejornada.ajustes;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.ajustes.model.ApiAjusteMarcacao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 02/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiAjusteMarcacaoService extends BaseIntegracaoService {
    @NotNull
    private static final String TAG = ApiAjusteMarcacaoService.class.getSimpleName();
    @NotNull
    private final ApiAjusteMarcacaoDao dao = new ApiAjusteMarcacaoDaoImpl();

    @NotNull
    public List<ApiAjusteMarcacao> getAjustesMarcacaoRealizados(
            final String tokenIntegracao,
            final Long codUltimoAjusteMarcacaoSincronizado) throws ProLogException {
        try {
            ensureValidToken(tokenIntegracao, TAG);
            return dao.getAjustesMarcacaoRealizados(tokenIntegracao, codUltimoAjusteMarcacaoSincronizado);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar ajustes de marcações\n" +
                    "codUltimoAjusteMarcacaoSincronizado: " + codUltimoAjusteMarcacaoSincronizado, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar ajustes de marcações");
        }
    }
}
