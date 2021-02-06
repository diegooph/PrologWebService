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
    @NotNull
    private static final String TAG = VeiculoRelatorioService.class.getSimpleName();
    @NotNull
    private final VeiculoRelatorioDao dao = Injection.provideVeiculoRelatorioDao();

    void getListagemVeiculosByUnidadeCsv(final OutputStream out, final List<Long> codUnidades) {
        try {
            dao.getListagemVeiculosByUnidadeCsv(out, codUnidades);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório de listagem de veiculos (CSV)", throwable);
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    Report getListagemVeiculosByUnidadeReport(final List<Long> codUnidades) throws ProLogException {
        try {
            return dao.getListagemVeiculosByUnidadeReport(codUnidades);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório de listagem de veiculos (REPORT)", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar relatório, tente novamente");
        }
    }

    void getEvolucaoKmCsv(final OutputStream out,
                          final Long codEmpresa,
                          final Long codVeiculo) {
        try {
            dao.getEvolucaoKmCsv(out, codEmpresa, codVeiculo);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório de evolução de KM (CSV)", throwable);
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    Report getEvolucaoKmReport(final Long codEmpresa,
                               final Long codVeiculo) throws ProLogException {
        try {
            return dao.getEvolucaoKmReport(codEmpresa, codVeiculo);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao gerar relatório de evolução de KM (REPORT)", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao gerar relatório, tente novamente");
        }
    }
}
