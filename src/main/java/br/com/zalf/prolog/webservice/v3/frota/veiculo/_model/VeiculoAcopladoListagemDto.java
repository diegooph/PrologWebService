package br.com.zalf.prolog.webservice.v3.frota.veiculo._model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2021-06-07
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Data
@Value
public class VeiculoAcopladoListagemDto {
    @ApiModelProperty(value = "Código do veículo acoplado", required = true, example = "1")
    @NotNull
    Long codVeiculoAcoplado;
    @ApiModelProperty(value = "Placa do veículo acoplado. Esse campo não possui nenhuma formatação.",
                      required = true,
                      example = "PRO1111")
    @NotNull
    String placaVeiculoAcoplado;
    @ApiModelProperty(value = "Identificador de frota do veículo Acoplado. Esse campo não possui nenhuma formatação.",
                      example = "FROTA01")
    @Nullable
    String identificadorFrotaAcoplado;
    @ApiModelProperty(value = "Indica se o veículo possui motor. Caso possua: TRUE, se não possuir: FALSE",
                      required = true, example = "true")
    boolean motorizado;
    @ApiModelProperty(value = "Indica qual é a posição que o veículo se encontra acoplado.",
                      required = true, example = "1")
    Short posicaoAcoplado;
}
