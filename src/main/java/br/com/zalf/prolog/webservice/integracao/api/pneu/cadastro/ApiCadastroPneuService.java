package br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.integracao.api.checklist.ApiChecklistDao;
import br.com.zalf.prolog.webservice.integracao.api.checklist.ApiChecklistDaoImpl;
import br.com.zalf.prolog.webservice.integracao.api.checklist.ApiChecklistService;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 13/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class ApiCadastroPneuService extends BaseIntegracaoService {
    @NotNull
    private static final String TAG = ApiCadastroPneuService.class.getSimpleName();
    @NotNull
    private ApiCadastroPneuDao dao = new ApiCadastroPneuDaoImpl();

    @NotNull
    Long insertPneuCadastro(final String tokenIntegracao, final ApiPneuCadastro pneuCadastro) {
        try {
            ensureValidToken(tokenIntegracao, TAG);
            return dao.insertPneuCadastro(tokenIntegracao, pneuCadastro);
        } catch (final Throwable t) {
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível cadastrar o pneu no ProLog");
        }
    }
}
