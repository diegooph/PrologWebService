package br.com.zalf.prolog.webservice.raizen.produtividade.relatorios;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;

/**
 * Created on 31/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeRelatorioService {
    private static final String TAG = RaizenProdutividadeRelatorioService.class.getSimpleName();
    @NotNull
    private RaizenProdutividadeRelatorioDao dao = Injection.provideRaizenProdutividadeRelatorioDao();

    public void getDadosGeraisProdutividadeCsv(@NotNull final OutputStream out,
                                               @NotNull final Long codEmpresa,
                                               @NotNull final String dataInicial,
                                               @NotNull final String dataFinal) {
        try {
            dao.getDadosGeraisProdutividadeCsv(
                    out,
                    codEmpresa,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório dos dados da produtividade da Raízen (CSV)", throwable);
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    public Report getDadosGeraisProdutividadeReport(@NotNull final Long codEmpresa,
                                                    @NotNull final String dataInicial,
                                                    @NotNull final String dataFinal) throws ProLogException {
        try {
            return dao.getDadosGeraisProdutividadeReport(
                    codEmpresa,
                    ProLogDateParser.toLocalDate(dataInicial),
                    ProLogDateParser.toLocalDate(dataFinal));
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório dos dados da produtividade da Raízen (REPORT)", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar relatório, tente novamente");
        }
    }
}