package br.com.zalf.prolog.webservice.frota.veiculo.historico.relatorio;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;

/**
 * Created on 2020-09-29
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public interface HistoricoEdicaoVeiculoRelatorioDao {

    void getHistoricoEdicaoVeiculoCsv(@NotNull OutputStream outputStream,
                                      @NotNull final Long codEmpresa,
                                      @NotNull final Long codVeiculo) throws Throwable;
}
