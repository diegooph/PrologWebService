package br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model;

import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoTipoProcesso;
import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Value
public class AlteracaoKmProcessoDto {
    @NotNull(message = "O código de empresa não pode ser nulo.")
    Long codEmpresa;
    @NotNull(message = "O código do veículo não pode ser nulo.")
    Long codVeiculo;
    @NotNull(message = "O código do processo não pode ser nulo.")
    Long codProcesso;
    @NotNull(message = "O tipo do processo não pode ser nulo.")
    VeiculoTipoProcesso tipoProcesso;
    @NotNull(message = "O novo km não pode ser nulo.")
    @PositiveOrZero(message = "O novo km não pode ser um valor negativo.")
    Long novoKm;
}
