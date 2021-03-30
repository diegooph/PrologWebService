package br.com.zalf.prolog.webservice.frota.v3.veiculo._model;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

/**
 * Created on 2019-10-04
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class VeiculoCadastroDto {
    @NotNull(message = "O código da empresa é obrigatório.")
    private final Long codEmpresaAlocado;
    @NotNull(message = "O código da unidade é obrigatório.")
    private final Long codUnidadeAlocado;
    @Size(min = 1, max = 7, message = "A placa deve conter entre 1 e 7 caracteres.")
    private final String placaVeiculo;
    @Nullable
    private final String identificadorFrota;
    @NotNull(message = "O código do modelo do veículo é obrigatório.")
    private final Long codModeloVeiculo;
    @NotNull(message = "O código do tipo do veículo é obrigatório.")
    private final Long codTipoVeiculo;
    @PositiveOrZero(message = "O KM fornecido não pode ser um número negativo.")
    private final long kmAtualVeiculo;
    @NotNull(message = "A flag 'possui hubodômetro' é obrigatória.")
    private final Boolean possuiHubodometro;
}
