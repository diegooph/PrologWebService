package br.com.zalf.prolog.webservice.integracao.api.unidade;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 18/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiUnidadeService extends BaseIntegracaoService {
    @NotNull
    private static final String TAG = ApiUnidadeService.class.getSimpleName();
    @NotNull
    private final ApiUnidadeDao dao = new ApiUnidadeDaoImpl();

    @NotNull
    public List<ApiUnidade> getUnidades(final String tokenIntegracao,
                                        final boolean apenasUnidadesAtivas) throws ProLogException {
        try {
            ensureValidToken(tokenIntegracao, TAG);
            return dao.getUnidades(tokenIntegracao, apenasUnidadesAtivas);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao listar unidades do ProLog:\n" +
                    "tokenIntegracao: " + tokenIntegracao + "\n" +
                    "apenasUnidadesAtivas: " + apenasUnidadesAtivas, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao listar unidades do ProLog");
        }
    }
}
