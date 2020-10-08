package br.com.zalf.prolog.webservice.frota.veiculo.historico.relatorio;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;

/**
 * Created on 2020-09-29
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public interface HistoricoEdicaoVeiculoRelatorioDao {
    /**
     * Busca o histórico de edições de um veiculo baseado em um código de veiculo e um código de empresa, retornando-o
     * em formato csv.
     *
     * @param outputStream arquivo binário onde o csv será escrito.
     * @param codVeiculo   um código de um veiculo para buscar o histórico.
     * @param codEmpresa   o código da empresa do histórico desejado.
     * @throws Throwable caso qualquer erro ocorrer.
     */
    void getHistoricoEdicaoVeiculoCsv(@NotNull OutputStream outputStream,
                                      @NotNull final Long codEmpresa,
                                      @NotNull final Long codVeiculo) throws Throwable;
}
