package br.com.zalf.prolog.webservice.integracao.api.afericao;


import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.integracao.api.afericao._model.AfericaoRealizada;
import br.com.zalf.prolog.webservice.integracao.praxio.IntegracaoPraxioService;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2020-05-06
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
public final class ApiAfericaoService extends BaseIntegracaoService {

    @NotNull
    private static final String TAG = IntegracaoPraxioService.class.getSimpleName();

    @NotNull
    private final ApiAfericaoDao dao = new ApiAfericaoDaoImpl();

    @NotNull
    public List<AfericaoRealizada> getAfericoesRealizadas(final String tokenIntegracao,
                                                          final Long codUltimaAfericao) {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (codUltimaAfericao == null) {
                throw new GenericException("Um código para a busca deve ser fornecido");
            }
            ensureValidToken(tokenIntegracao, TAG);
            return dao.getAfericoesRealizadas(tokenIntegracao, codUltimaAfericao);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar as novas aferições da Integração\n" +
                    "Código da última aferição sincronizada: %d", codUltimaAfericao), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar aferições para sincronizar");
        }
    }
}
