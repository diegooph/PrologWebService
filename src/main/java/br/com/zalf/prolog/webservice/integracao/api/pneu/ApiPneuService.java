package br.com.zalf.prolog.webservice.integracao.api.pneu;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiMarcaBanda;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiMarcaPneu;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiModeloBanda;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiModeloPneu;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 16/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class ApiPneuService extends BaseIntegracaoService {
    @NotNull
    private static final String TAG = ApiPneuService.class.getSimpleName();
    @NotNull
    private ApiPneuDao dao = new ApiPneuDaoImpl();

    @NotNull
    public List<ApiMarcaPneu> getMarcasPneu(final String tokenIntegracao,
                                            final boolean apenasMarcasPneuAtivas) throws ProLogException {
        try {
            ensureValidToken(tokenIntegracao, TAG);
            return dao.getMarcasPneu(tokenIntegracao, apenasMarcasPneuAtivas);
        } catch (final Throwable t) {
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível buscar as marcas de pneu disponíveis");
        }
    }

    @NotNull
    public List<ApiModeloPneu> getModelosPneu(final String tokenIntegracao,
                                              final Long codMarcaPneu,
                                              final boolean apenasModelosPneuAtivos) throws ProLogException {
        try {
            ensureValidToken(tokenIntegracao, TAG);
            return dao.getModelosPneu(tokenIntegracao, codMarcaPneu, apenasModelosPneuAtivos);
        } catch (final Throwable t) {
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível buscar os modelos de pneu disponíveis");
        }
    }

    @NotNull
    public List<ApiMarcaBanda> getMarcasBanda(final String tokenIntegracao,
                                              final boolean apenasMarcasBandaAtivas) throws ProLogException {
        try {
            ensureValidToken(tokenIntegracao, TAG);
            return dao.getMarcasBanda(tokenIntegracao, apenasMarcasBandaAtivas);
        } catch (final Throwable t) {
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível buscar as marcas de banda disponíveis");
        }
    }

    @NotNull
    public List<ApiModeloBanda> getModelosBanda(final String tokenIntegracao,
                                                final Long codMarcaBanda,
                                                final boolean apenasModelosBandaAtivos) {
        try {
            ensureValidToken(tokenIntegracao, TAG);
            return dao.getModelosBanda(tokenIntegracao, codMarcaBanda, apenasModelosBandaAtivos);
        } catch (final Throwable t) {
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível buscar os modelos de banda disponíveis");
        }
    }
}
