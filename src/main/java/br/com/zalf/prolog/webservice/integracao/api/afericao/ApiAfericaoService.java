package br.com.zalf.prolog.webservice.integracao.api.afericao;


import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.date.PrologDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.integracao.api.afericao._model.ApiPneuMedicaoRealizada;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static br.com.zalf.prolog.webservice.commons.util.StringUtils.isNullOrEmpty;
import static br.com.zalf.prolog.webservice.commons.util.StringUtils.trimToNull;

/**
 * Created on 2020-05-06
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
public final class ApiAfericaoService extends BaseIntegracaoService {
    @NotNull
    private static final String TAG = ApiAfericaoService.class.getSimpleName();
    @NotNull
    private final ApiAfericaoDao dao = new ApiAfericaoDaoImpl();

    @NotNull
    public List<ApiPneuMedicaoRealizada> getAfericoesRealizadas(final String tokenIntegracao,
                                                                final Long codigoProcessoAfericao,
                                                                final String dataHoraUltimaAtualizacaoUtc) {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }

            if (codigoProcessoAfericao != null && !isNullOrEmpty(trimToNull(dataHoraUltimaAtualizacaoUtc))) {
                throw new GenericException("É permitido apenas um parâmetro de busca");
            }

            if (codigoProcessoAfericao == null && isNullOrEmpty(trimToNull(dataHoraUltimaAtualizacaoUtc))) {
                throw new GenericException("É obrigatório fornecer pelo menos um parâmetro de busca");
            }
            ensureValidToken(tokenIntegracao, TAG);
            return dao.getAfericoesRealizadas(
                    tokenIntegracao,
                    codigoProcessoAfericao,
                    isNullOrEmpty(trimToNull(dataHoraUltimaAtualizacaoUtc))
                            ? null
                            : PrologDateParser.toLocalDateTime(dataHoraUltimaAtualizacaoUtc));
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar as novas aferições da Integração\n" +
                            "tokenIntegracao: %s\n" +
                            "codigoProcessoAfericao: %d\n" +
                            "dataHoraUltimaAtualizacaoUtc: %s",
                    tokenIntegracao, codigoProcessoAfericao, dataHoraUltimaAtualizacaoUtc), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar aferições realizadas para sincronizar");
        }
    }
}
