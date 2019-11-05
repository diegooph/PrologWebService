package br.com.zalf.prolog.webservice.integracao.api.controlejornada;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada._model.ApiMarcacao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 30/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiMarcacaoService extends BaseIntegracaoService {
    @NotNull
    private static final String TAG = ApiMarcacaoService.class.getSimpleName();
    @NotNull
    private final ApiMarcacaoDao dao = new ApiMarcacaoDaoImpl();

    @NotNull
    public List<ApiMarcacao> getMarcacoesRealizadas(
            final String tokenIntegracao,
            final Long codUltimaMarcacaoSincronizada) throws ProLogException {
        try {
            ensureValidToken(tokenIntegracao, TAG);
            return dao.getMarcacoesRealizadas(tokenIntegracao, codUltimaMarcacaoSincronizada);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar marcações\n" +
                    "codUltimaMarcacaoSincronizada: " + codUltimaMarcacaoSincronizada, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar marcações");
        }
    }
}
