package br.com.zalf.prolog.webservice.integracao.api.controlejornada.tipomarcacao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.tipomarcacao._model.ApiTipoMarcacao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 29/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiTipoMarcacaoService extends BaseIntegracaoService {
    @NotNull
    private static final String TAG = ApiTipoMarcacaoService.class.getSimpleName();
    @NotNull
    private final ApiTipoMarcacaoDao dao = new ApiTipoMarcacaoDaoImpl();

    @NotNull
    public List<ApiTipoMarcacao> getTiposMarcacoes(final String tokenIntegracao,
                                                   final boolean apenasTiposMarcacoesAtivos) throws ProLogException {
        try {
            ensureValidToken(tokenIntegracao, TAG);
            return dao.getTiposMarcacoes(tokenIntegracao, apenasTiposMarcacoesAtivos);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar os tipos de marcações\n" +
                    "tokenIntegracao: " + tokenIntegracao + "\n" +
                    "apenasTiposMarcacoesAtivos: " + apenasTiposMarcacoesAtivos, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar os tipos de marcações");
        }
    }
}
