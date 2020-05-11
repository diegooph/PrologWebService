package br.com.zalf.prolog.webservice.integracao.api.afericao;


import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.integracao.api.afericao._model.ApiPneuMedicaoRealizada;
import br.com.zalf.prolog.webservice.integracao.praxio.IntegracaoPraxioService;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public List<ApiPneuMedicaoRealizada> getAfericoesRealizadas(@NotNull final String tokenIntegracao,
                                                                final Long codigoProcessoAfericao,
                                                                final String dataHoraUltimaAtualizacaoUtc) {
        try {
            LocalDateTime dataHora = null;
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }

            if ((codigoProcessoAfericao != null) && (!dataHoraUltimaAtualizacaoUtc.isEmpty())) {
                throw new GenericException("É obrigatório fornecer apenas um parâmetro @QueryParam");
            }

            if ((codigoProcessoAfericao == null) && (dataHoraUltimaAtualizacaoUtc.isEmpty())) {
                throw new GenericException("É obrigatório fornecer pelo menos um parâmetro @QueryParam");
            }

            if (!dataHoraUltimaAtualizacaoUtc.isEmpty()) {
                // Eu formatei usando o ProLogDateParser mas não da certo porque ele corta os milissegundos.
                // Usei esse parse para deixar com 3 casas nos milissegundos.
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                dataHora = LocalDateTime.parse(dataHoraUltimaAtualizacaoUtc, formato);
            }
            ensureValidToken(tokenIntegracao, TAG);
            return dao.getAfericoesRealizadas(tokenIntegracao, codigoProcessoAfericao, dataHora);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar as novas aferições da Integração\n" +
                    "Código da última aferição sincronizada: %d", codigoProcessoAfericao), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar aferições para sincronizar");
        }
    }
}
