package br.com.zalf.prolog.webservice.frota.veiculo.historico;

import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.HistoricoEdicaoVeiculo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2020-09-14
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public interface HistoricoEdicaoVeiculoDao {
    /**
     * Busca o histórico de edições de um veiculo baseado em um código de veiculo e um código de empresa.
     *
     * @param codVeiculo um código de um veiculo para buscar o histórico.
     * @param codEmpresa o código da empresa do histórico desejado.
     * @return uma lista de {@link HistoricoEdicaoVeiculo históricos} com o histórico e algumas informações pertinentes.
     * @throws Throwable caso qualquer erro ocorrer.
     */
    @NotNull
    List<HistoricoEdicaoVeiculo> getHistoricoEdicaoVeiculo(@NotNull final Long codEmpresa,
                                                           @NotNull final Long codVeiculo) throws Throwable;
}
