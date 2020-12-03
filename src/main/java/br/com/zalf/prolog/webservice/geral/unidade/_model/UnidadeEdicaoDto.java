package br.com.zalf.prolog.webservice.geral.unidade._model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@ApiModel(description = "Dados a serem usados na atualização de uma unidade. Aqui os campos precisam ser todos " +
        "preenchidos, porque todos são atualizados. Isso significa que mesmo que a única mudança seja nomeUnidade," +
        "precisa-se enviar também codAuxiliar antigo, latitudadeUnidade antiga e longitudeUnidade antiga, pois elas" +
        "farão parte do update.")
@Builder
@Getter
public final class UnidadeEdicaoDto {
    @ApiModelProperty(
            value = "Um código de unidade, que será utilizado como chave para saber qual unidade sofrerá as " +
                    "atualizações.",
            example = "215"
    )
    @NotNull(message = "O código da unidade é obrigatório.")
    private final Long codUnidade;
    @ApiModelProperty(
            value = "Um nome para a unidade.",
            example = "Unidade de testes"
    )
    @NotBlank(message = "O nome da unidade não pode estar vazio.")
    @Size(max = 40, message = "O nome da unidade precisa conter até 40 caracteres.")
    private final String nomeUnidade;
    @ApiModelProperty(
            value = "Um código auxiliar para a unidade unidade.",
            example = "01:01"
    )
    @Nullable
    private final String codAuxiliarUnidade;
    @ApiModelProperty(
            value = "A latitude da localização da unidade em questão.",
            example = "-27.641369"
    )
    @Nullable
    private final String latitudeUnidade;
    @ApiModelProperty(
            value = "A latitude da localização da unidade em questão.",
            example = "-48.679233"
    )
    @Nullable
    private final String longitudeUnidade;
}
