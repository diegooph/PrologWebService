package br.com.zalf.prolog.webservice.v3.frota.veiculo._model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2021-06-07
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Data
public class VeiculosAcopladosListagemDto {
    @ApiModelProperty(value = "Código do processo de acoplamento", required = true, example = "1")
    @NotNull
    Long codProcessoAcoplamento;
    @ApiModelProperty(value = "Caso hajam veículos acoplados, lista quais são separados pelo código de acoplamento.")
    @NotNull
    List<VeiculoAcopladoListagemDto> veiculosAcoplados;
}
