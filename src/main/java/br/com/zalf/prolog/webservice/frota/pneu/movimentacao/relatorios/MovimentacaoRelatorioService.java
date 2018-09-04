package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.relatorios;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;

/**
 * Created on 04/09/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class MovimentacaoRelatorioService {
    private static final String TAG = MovimentacaoRelatorioService.class.getSimpleName();
    @NotNull
    private MovimentacaoRelatorioDao dao = Injection.provideMovimentacaoRelatorioDao();

    public void getDadosGeraisAfericaoCsv(@NotNull final OutputStream out,
                                          @NotNull final Long codUnidade,
                                          @NotNull final String dataInicial,
                                          @NotNull final String dataFinal) {
        try {
            dao.getDadosGeraisMovimentacaoCsv(
                    out,
                    codUnidade,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório das movimentações (CSV)", throwable);
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    public Report getDadosGeraisAfericaoReport(@NotNull final Long codUnidade,
                                               @NotNull final String dataInicial,
                                               @NotNull final String dataFinal) throws ProLogException {
        try {
            return dao.getDadosGeraisMovimentacaoReport(
                    codUnidade,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório das movimentações (REPORT)", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar relatório, tente novamente");
        }
    }
}
