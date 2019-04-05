package br.com.zalf.prolog.webservice.colaborador.relatorios;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.util.List;

/**
 * Created on 05/04/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class ColaboradorRelatorioService {
    private static final String TAG = ColaboradorRelatorioService.class.getSimpleName();
    @NotNull
    private ColaboradorRelatorioDao dao = Injection.provideColaboradorRelatorioDao();

    public void getListagemColaboradoresByUnidadeCsv(@NotNull final OutputStream out,
                                                     @NotNull final List<Long> codUnidades,
                                                     @NotNull final String userToken) {
        try {
            dao.getListagemColaboradoresByUnidadeCsv(
                    out,
                    codUnidades,
                    userToken);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório do cronograma de aferições (CSV)", throwable);
            throw new RuntimeException(throwable);
        }
    }

    public Report getListagemColaboradoresByUnidadeReport(@NotNull final List<Long> codUnidades,
                                                          @NotNull final String userToken) throws ProLogException {
        try {
            return dao.getListagemColaboradoresByUnidadeReport(
                    codUnidades, userToken);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório de listagem de colaoradores (REPORT)", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar relatório, tente novamente");
        }
    }
}
