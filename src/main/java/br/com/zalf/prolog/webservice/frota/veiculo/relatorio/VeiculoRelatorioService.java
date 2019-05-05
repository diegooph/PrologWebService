package br.com.zalf.prolog.webservice.frota.veiculo.relatorio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.util.List;

/**
 * Created on 02/05/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class VeiculoRelatorioService {
    private static final String TAG = VeiculoRelatorioService.class.getSimpleName();
    @NotNull
    private VeiculoRelatorioDao dao = Injection.provideVeiculoRelatorioDao();

    public void getListagemVeiculosByUnidadeCsv(final OutputStream out,
                                                final List<Long> codUnidades) {
        try {
            dao.getListagemVeiculosByUnidadeCsv(
                    out,
                    codUnidades);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório de listagem de veiculos (CSV)", throwable);
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    public Report getListagemVeiculosByUnidadeReport(final List<Long> codUnidades) throws ProLogException {
        try {
            return dao.getListagemVeiculosByUnidadeReport(codUnidades);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório de listagem de veiculos (REPORT)", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar relatório, tente novamente");
        }
    }
}
