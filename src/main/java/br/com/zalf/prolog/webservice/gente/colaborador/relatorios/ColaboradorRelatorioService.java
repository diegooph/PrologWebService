package br.com.zalf.prolog.webservice.gente.colaborador.relatorios;

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

    public void getListagemColaboradoresByUnidadeCsv(final OutputStream out,
                                                     final List<Long> codUnidades) {
        try {
            dao.getListagemColaboradoresByUnidadeCsv(
                    out,
                    codUnidades);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório de listagem de colaboradores (CSV)", throwable);
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    public Report getListagemColaboradoresByUnidadeReport(final List<Long> codUnidades) throws ProLogException {
        try {
            return dao.getListagemColaboradoresByUnidadeReport(codUnidades);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório de listagem de colaboradores (REPORT)", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar relatório, tente novamente");
        }
    }
}
