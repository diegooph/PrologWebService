package br.com.zalf.prolog.webservice.frota.veiculo.historico.relatorio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.report.CsvWriter;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;

/**
 * Created on 2020-09-29
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class HistoricoEdicaoVeiculoRelatorioDaoImpl implements HistoricoEdicaoVeiculoRelatorioDao {
    @Override
    public void getHistoricoEdicaoVeiculoCsv(@NotNull final OutputStream outputStream,
                                             @NotNull final Long codEmpresa,
                                             @NotNull final Long codVeiculo) throws Throwable {
        new CsvWriter
                .Builder(outputStream)
                .withCsvReport(new HistoricoEdicaoVeiculoCsv(
                        Injection
                                .provideHistoricoEdicaoVeiculoDao()
                                .getHistoricoEdicaoVeiculo(codEmpresa, codVeiculo)))
                .build()
                .write();
    }
}
