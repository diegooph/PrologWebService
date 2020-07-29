package br.com.zalf.prolog.webservice.integracao.api.pneu;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.ApiPneuAlteracaoStatus;
import br.com.zalf.prolog.webservice.integracao.api.pneu.model.DiagramaPosicaoMapeado;
import br.com.zalf.prolog.webservice.integracao.response.PosicaoPneuMepadoResponse;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

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
            // Removemos atualizações que tem código de unidade bloqueado. Estes não serão processados.
            final List<Long> codUnidadesBloquedas = Injection
                    .provideIntegracaoDao()
                    .getCodUnidadesIntegracaoBloqueadaByTokenIntegracao(
                            tokenIntegracao,
                            SistemaKey.API_PROLOG,
                            RecursoIntegrado.PNEUS);
            pneusAtualizacaoStatus.removeIf(pneu -> codUnidadesBloquedas.contains(pneu.getCodUnidadePneu()));
            dao.atualizaStatusPneus(tokenIntegracao, pneusAtualizacaoStatus);
            return new SuccessResponseIntegracao("Pneus atualizados com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, "Não foi possível atualizar o status dos pneus:\n" +
                    "tokenIntegracao: " + tokenIntegracao, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível atualizar o status dos pneus");
        }
    }

    @NotNull
    public List<PosicaoPneuMepadoResponse> validaPosicoesMapeadasSistemaParceiro(
            final String tokenIntegracao,
            final List<DiagramaPosicaoMapeado> diagramasPosicoes) throws ProLogException {
        try {
            ensureValidToken(tokenIntegracao, TAG);
            return dao.validaPosicoesMapeadasSistemaParceiro(tokenIntegracao, diagramasPosicoes);
        } catch (final Throwable t) {
            Log.e(TAG, "Não foi possível validar as posições do veículo:\n" +
                    "tokenIntegracao: " + tokenIntegracao, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Não foi possível validar as posições do veículo");
        }
    }
}
