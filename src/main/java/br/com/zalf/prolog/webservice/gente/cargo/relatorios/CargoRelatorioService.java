package br.com.zalf.prolog.webservice.gente.cargo.relatorios;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.util.List;

/**
 * Created on 25/03/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class CargoRelatorioService {
    private static final String TAG = CargoRelatorioService.class.getSimpleName();
    @NotNull
    private final CargoRelatorioDao dao = Injection.providePermissaoRelatorioDao();

    public void getPermissoesDetalhadasCsv(@NotNull final OutputStream out,
                                           @NotNull final List<Long> codUnidades) throws ProLogException {
        try {
            dao.getPermissoesDetalhadasCsv(
                    out,
                    codUnidades);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório de permissões detalhadas (CSV)", throwable);
            throw new RuntimeException(throwable);
        }
    }

    public Report getPermissoesDetalhadasReport(@NotNull final List<Long> codUnidades) throws ProLogException {
        try {
            return dao.getPermissoesDetalhadasReport(
                    codUnidades);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório de permissões detalhadas (REPORT)", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar relatório, tente novamente");
        }
    }
}
