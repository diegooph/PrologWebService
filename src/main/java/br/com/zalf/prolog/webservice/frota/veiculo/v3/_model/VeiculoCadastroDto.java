package br.com.zalf.prolog.webservice.frota.veiculo.v3._model;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;

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
    @NotNull(message = "A placa do veículo é obrigatória.")
    private final String placaVeiculo;
    @Nullable
    private final String identificadorFrota;
    @NotNull(message = "O código do modelo do veículo é obrigatório.")
    private final Long codModeloVeiculo;
    @NotNull(message = "O código do modelo do tipo do veículo é obrigatório.")
    private final Long codTipoVeiculo;
    private final long kmAtualVeiculo;
    @NotNull(message = "A flag 'possui hubodômetro' é obrigatória.")
    private final Boolean possuiHubodometro;
}
