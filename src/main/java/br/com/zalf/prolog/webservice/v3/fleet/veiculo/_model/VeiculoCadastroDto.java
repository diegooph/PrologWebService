package br.com.zalf.prolog.webservice.v3.fleet.veiculo._model;

import br.com.zalf.prolog.webservice.v3.validation.IdBranch;
import br.com.zalf.prolog.webservice.v3.validation.IdCompany;
import io.swagger.annotations.ApiModelProperty;
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
    @IdCompany
    @ApiModelProperty(value = "Código da empresa onde o veículo será cadastrado.", required = true, example = "10")
    @NotNull(message = "O código da empresa não pode ser nulo.")
    private final Long codEmpresaAlocado;
    @IdBranch
    @ApiModelProperty(value = "Código da unidade onde o veículo será cadastrado.", required = true, example = "215")
    @NotNull(message = "O código da unidade não pode ser nulo.")
    private final Long codUnidadeAlocado;
    @ApiModelProperty(value = "Placa do veículo. Esse campo não possui nenhuma formatação.",
                      required = true,
                      example = "PRO1102")
    @Size(min = 1, max = 7, message = "A placa deve conter entre 1 e 7 caracteres.")
    @NotNull(message = "A placa do veículo não pode ser nulo.")
    private final String placaVeiculo;
    @ApiModelProperty(value = "Identificador de frota do veículo. Esse campo não possui nenhuma formatação.",
                      example = "FROTA01")
    @Nullable
    private final String identificadorFrota;
    @ApiModelProperty(value = "Código do modelo do veículo.", required = true, example = "590")
    @NotNull(message = "O código do modelo do veículo não pode ser nulo.")
    private final Long codModeloVeiculo;
    @ApiModelProperty(value = "Código do tipo do veículo. O tipo do veículo define a estrutura de eixos e pneus que o" +
            " veículo tem.",
                      required = true,
                      example = "590")
    @NotNull(message = "O código do tipo do veículo não pode ser nulo.")
    private final Long codTipoVeiculo;
    @ApiModelProperty(value = "Km do veículo.", required = true, example = "111111")
    @PositiveOrZero(message = "O KM fornecido não pode ser um número negativo.")
    private final long kmAtualVeiculo;
    @ApiModelProperty(value = "Flag indicando se o veículo possui hubodômetro. Só deve ser 'true' em cenários de " +
            "carretas ou veículos não motorizados que possuam hubodômetro próprio. Essa flag impacta diretamente na " +
            "coleta de quilometragem de todos os processos do Prolog.",
                      required = true,
                      example = "111111")
    @NotNull(message = "A flag 'possui hubodômetro' não pode ser nulo.")
    private final Boolean possuiHubodometro;
}
