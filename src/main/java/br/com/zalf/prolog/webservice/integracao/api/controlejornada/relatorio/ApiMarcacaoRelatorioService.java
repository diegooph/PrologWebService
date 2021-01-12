package br.com.zalf.prolog.webservice.integracao.api.controlejornada.relatorio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.PrologDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.integracao.api.controlejornada.relatorio._model.ApiMarcacaoRelatorio1510;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 11/5/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiMarcacaoRelatorioService extends BaseIntegracaoService {
    @NotNull
    private static final String TAG = ApiMarcacaoRelatorioService.class.getSimpleName();
    @NotNull
    private final ApiMarcacaoRelatorioDao dao = new ApiMarcacaoRelatorioDaoImpl();

    @NotNull
    public List<ApiMarcacaoRelatorio1510> getRelatorioPortaria1510(
            @NotNull final String tokenIntegracao,
            @NotNull final String dataInicial,
            @NotNull final String dataFinal,
            @Nullable final Long codUnidadeProLog,
            @Nullable final Long codTipoMarcacao,
            @Nullable final String cpfColaborador) throws ProLogException {
        try {
            ensureValidToken(tokenIntegracao, TAG);
            return dao.getRelatorioPortaria1510(
                    tokenIntegracao,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal),
                    codUnidadeProLog,
                    codTipoMarcacao,
                    cpfColaborador);
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar relatório padrão portaria 1510\n" +
                            "tokenIntegracao: " + tokenIntegracao + "\n" +
                            "dataInicial: " + dataInicial + "\n" +
                            "dataFinal: " + dataFinal + "\n" +
                            "codUnidadeProLog: " + codUnidadeProLog + "\n" +
                            "codTipoMarcacao: " + codTipoMarcacao + "\n" +
                            "cpfColaborador: " + cpfColaborador,
                    t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar dados do relatório padrão portaria 1510");
        }
    }
}
