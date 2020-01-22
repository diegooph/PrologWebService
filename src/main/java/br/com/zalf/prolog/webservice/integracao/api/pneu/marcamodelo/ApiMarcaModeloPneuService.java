package br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiMarcaBanda;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiMarcaPneu;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiModeloBanda;
import br.com.zalf.prolog.webservice.integracao.api.pneu.marcamodelo.model.ApiModeloPneu;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 21/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiMarcaModeloPneuService extends BaseIntegracaoService {
    @NotNull
    private static final String TAG = ApiMarcaModeloPneuService.class.getSimpleName();
    @NotNull
    private final ApiMarcaModeloPneuDao dao = new ApiMarcaModeloPneuDaoImpl();

    @NotNull
    public List<ApiMarcaPneu> getMarcasPneu(final String tokenIntegracao,
                                            final boolean apenasMarcasPneuAtivas) throws ProLogException {
        try {
            ensureValidToken(tokenIntegracao, TAG);
            return dao.getMarcasPneu(tokenIntegracao, apenasMarcasPneuAtivas);
        } catch (final Throwable t) {
            Log.e(TAG, "Não foi possível buscar as marcas de pneu disponíveis:\n" +
                    "tokenIntegracao: " + tokenIntegracao + "\n" +
                    "apenasMarcasPneuAtivas: " + apenasMarcasPneuAtivas, t);
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
            Log.e(TAG, "Não foi possível buscar os modelos de pneu disponíveis:\n" +
                    "tokenIntegracao: " + tokenIntegracao + "\n" +
                    "codMarcaPneu: " + codMarcaPneu + "\n" +
                    "apenasModelosPneuAtivos: " + apenasModelosPneuAtivos, t);
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
            Log.e(TAG, "Não foi possível buscar as marcas de banda disponíveis:\n" +
                    "tokenIntegracao: " + tokenIntegracao + "\n" +
                    "apenasMarcasBandaAtivas: " + apenasMarcasBandaAtivas, t);
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
            Log.e(TAG, "Não foi possível buscar os modelos de banda disponíveis:\n" +
                    "tokenIntegracao: " + tokenIntegracao + "\n" +
                    "codMarcaBanda: " + codMarcaBanda + "\n" +
                    "apenasModelosBandaAtivos: " + apenasModelosBandaAtivos, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível buscar os modelos de banda disponíveis");
        }
    }
}
