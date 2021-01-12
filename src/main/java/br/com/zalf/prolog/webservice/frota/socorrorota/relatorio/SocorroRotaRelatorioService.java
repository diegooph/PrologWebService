package br.com.zalf.prolog.webservice.frota.socorrorota.relatorio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.date.PrologDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.util.List;

/**
 * Created on 12/02/20.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class SocorroRotaRelatorioService {
    private static final String TAG = SocorroRotaRelatorioService.class.getSimpleName();
    @NotNull
    private final SocorroRotaRelatorioDao dao = Injection.provideSocorroRotaRelatorioDao();

    public void getDadosGeraisSocorrosRotasCsv(@NotNull final OutputStream out,
                                               @NotNull final List<Long> codUnidades,
                                               @NotNull final String dataInicial,
                                               @NotNull final String dataFinal,
                                               @NotNull final List<String> statusSocorrosRotas) {
        try {
            dao.getDadosGeraisSocorrosRotasCsv(
                    out,
                    codUnidades,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal),
                    statusSocorrosRotas);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório de dados gerais dos socorros em rotas (CSV)", throwable);
            throw new RuntimeException(throwable);
        }

    }

    public Report getDadosGeraisSocorrosRotasReport(@NotNull final List<Long> codUnidades,
                                                    @NotNull final String dataInicial,
                                                    @NotNull final String dataFinal,
                                                    @NotNull final List<String> statusSocorrosRotas)
            throws ProLogException {
        try {
            return dao.getDadosGeraisSocorrosRotasReport(
                    codUnidades,
                    PrologDateParser.toLocalDate(dataInicial),
                    PrologDateParser.toLocalDate(dataFinal),
                    statusSocorrosRotas);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório de dados gerais dos socorros em rota (REPORT)", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar relatório, tente novamente");
        }
    }
}