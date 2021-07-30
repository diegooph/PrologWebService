package br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Created on 2021-05-01
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class TireServiceDto {
    @ApiModelProperty(value = "Código do serviço.", required = true, example = "12345")
    @NotNull
    private final Long codServico;
    @ApiModelProperty(value = "Nome do serviço.", required = true, example = "Recapagem")
    @NotNull
    private final String nomeServico;
    @ApiModelProperty(value = "Flag que indica se o serviço solicita uma troca de banda do pneu.",
                      required = true,
                      example = "true")
    @NotNull
    private final Boolean incrementaVida;
    @ApiModelProperty(value = "Custo para a realização do serviço.",
                      required = true,
                      example = "1000")
    @NotNull
    private final BigDecimal custoServico;
    @ApiModelProperty(value = "Vida do pneu no momento do serviço realizado. No Prolog, a vida do pneu é " +
            "um número de 1 à 11 onde 1 é usado para o pneu novo, nunca recapado.",
                      example = "1")
    @NotNull
    private final Integer vidaPneuMomentoServico;
}
