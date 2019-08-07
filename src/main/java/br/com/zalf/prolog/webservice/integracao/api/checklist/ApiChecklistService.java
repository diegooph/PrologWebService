package br.com.zalf.prolog.webservice.integracao.api.checklist;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 07/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiChecklistService extends BaseIntegracaoService {
    @NotNull
    private static final String TAG = ApiChecklistService.class.getSimpleName();
    @NotNull
    private ApiChecklistDao dao = new ApiChecklistDaoImpl();

    @NotNull
    List<ApiAlternativaModeloChecklist> getAlternativasModeloChecklist(final String tokenIntegracao) {
        try {
            ensureValidToken(tokenIntegracao, TAG);
            return dao.getAlternativasModeloChecklist(tokenIntegracao);
        } catch (final Throwable t) {
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar alternativas dos modelos de checklist");
        }
    }
}
