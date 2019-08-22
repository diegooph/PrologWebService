package br.com.zalf.prolog.webservice.integracao.api.pneu;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiPneuAlteracaoStatus;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 16/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiPneuService extends BaseIntegracaoService {
    @NotNull
    private static final String TAG = ApiPneuService.class.getSimpleName();
    @NotNull
    private final ApiPneuDao dao = new ApiPneuDaoImpl();

    @NotNull
    public SuccessResponseIntegracao atualizaStatusPneus(
            final String tokenIntegracao,
            final List<ApiPneuAlteracaoStatus> pneusAtualizacaoStatus) throws ProLogException {
        try {
            ensureValidToken(tokenIntegracao, TAG);
            dao.atualizaStatusPneus(tokenIntegracao, pneusAtualizacaoStatus);
            return new SuccessResponseIntegracao("Pneus atualizados com sucesso");
        } catch (final Throwable t) {
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível atualizar o status dos pneus");
        }
    }
}
