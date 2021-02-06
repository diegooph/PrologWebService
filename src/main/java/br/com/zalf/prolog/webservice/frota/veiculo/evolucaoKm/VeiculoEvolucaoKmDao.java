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

    Optional<VeiculoEvolucaoKmResponse> getVeiculoEvolucaoKm(@NotNull final Long codEmpresa,
                                                             @NotNull final Long codVeiculo,
                                                             @NotNull final LocalDate dataInicial,
                                                             @NotNull final LocalDate dataFinal) throws Throwable;
}