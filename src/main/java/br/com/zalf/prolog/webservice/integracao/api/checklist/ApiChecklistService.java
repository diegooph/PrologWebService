package br.com.zalf.prolog.webservice.integracao.api.checklist;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
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
    List<ApiAlternativaModeloChecklist> getAlternativasModeloChecklist(
            final String tokenIntegracao,
            final boolean apenasModelosAtivos,
            final boolean apenasPerguntasAtivas,
            final boolean apenasAlternativasAtivas) {
        try {
            ensureValidToken(tokenIntegracao, TAG);
            return dao.getAlternativasModeloChecklist(
                    tokenIntegracao,
                    apenasModelosAtivos,
                    apenasPerguntasAtivas,
                    apenasAlternativasAtivas);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar alternativas dos modelos de checklist:\n" +
                    "tokenIntegracao: " + tokenIntegracao + "\n" +
                    "apenasModelosAtivos: " + apenasModelosAtivos + "\n" +
                    "apenasPerguntasAtivas: " + apenasPerguntasAtivas + "\n" +
                    "apenasAlternativasAtivas: " + apenasAlternativasAtivas, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar alternativas dos modelos de checklist");
        }
    }
}
