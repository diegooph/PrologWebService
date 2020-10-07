package br.com.zalf.prolog.webservice.frota.veiculo.evolucaoKm;

import br.com.zalf.prolog.webservice.frota.veiculo.evolucaoKm._model.VeiculoEvolucaoKmResponse;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Created on 2020-10-07
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface VeiculoEvolucaoKmDao {

    /**
     * Busca a evolução de kms de um veículo pela placa, código da empresa e em um determinado intervalo de data.
     *
     * @param codEmpresa  Código da empresa para a qual as informações serão filtradas.
     * @param codVeiculo  Código do veículo para o qual as informações serão filtradas.
     * @param dataInicial Data inicial para a qual as informações serão filtradas.
     * @param dataFinal   Data final para a qual as informações serão filtradas.
     * @return um veículo e sua evolução de km.
     * @throws Throwable Se algum erro ocorrer.
     */
    Optional<VeiculoEvolucaoKmResponse> getVeiculoEvolucaoKm(@NotNull final Long codEmpresa,
                                                             @NotNull final Long codVeiculo,
                                                             @NotNull final LocalDate dataInicial,
                                                             @NotNull final LocalDate dataFinal) throws Throwable;
}