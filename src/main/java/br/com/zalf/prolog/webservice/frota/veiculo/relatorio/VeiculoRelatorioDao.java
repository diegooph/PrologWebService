package br.com.zalf.prolog.webservice.frota.veiculo.relatorio;

import br.com.zalf.prolog.webservice.commons.report.Report;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * Created on 1/25/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface VeiculoRelatorioDao {

    int getQtdVeiculosAtivos(@NotNull final List<Long> codUnidades) throws SQLException;

    void getListagemVeiculosByUnidadeCsv(@NotNull final OutputStream out,
                                         @NotNull final List<Long> codUnidades) throws Throwable;

    @NotNull
    Report getListagemVeiculosByUnidadeReport(@NotNull final List<Long> codUnidades) throws Throwable;

    void getEvolucaoKmCsv(@NotNull final OutputStream out,
                          @NotNull final Long codEmpresa,
                          @NotNull final Long codVeiculo) throws Throwable;

    @NotNull
    Report getEvolucaoKmReport(@NotNull final Long codEmpresa,
                               @NotNull final Long codVeiculo) throws Throwable;
}