package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;

/**
 * Created on 2020-09-03
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class AvaCorpAvilanService extends BaseIntegracaoService {
    @NotNull
    private final static String TAG = AvaCorpAvilanService.class.getSimpleName();
    @NotNull
    private final AvaCorpAvilanDao dao = new AvaCorpAvilanDaoImpl();

    public void getOrdensServicosPendentesSincroniaCsv(@NotNull final OutputStream outputStream,
                                                       @NotNull final String tokenIntegracao,
                                                       @Nullable final String dataInicial,
                                                       @Nullable final String dataFinal) {
        try {
            ensureValidToken(tokenIntegracao, TAG);
            dao.getOrdensServicosPendentesSincroniaCsv(
                    outputStream,
                    dataInicial != null ? ProLogDateParser.toLocalDate(dataInicial) : null,
                    dataFinal != null ? ProLogDateParser.toLocalDate(dataFinal) : null);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar Ordens de Serviços pendentes de sincronizar", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar Ordens de Serviços pendentes para sincronizar");
        }
    }
}
