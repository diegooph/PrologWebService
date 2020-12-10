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
@ApiModel(description = "Objeto utilizado para sobrescrever informações da Unidade. Se tratando de sobrescrita, caso " +
        "alguma propriedade não for fornecida, assumiremos null.\nPara sobrescrever apenas uma propriedade envie as " +
        "demais contendo o valor original.\nPara remover uma propriedade, envie null e as demais contendo o valor " +
        "original.")
@Builder
@Getter
public final class UnidadeEdicaoDto {
    @ApiModelProperty(
            value = "Código da unidade",
            example = "215")
    @NotNull(message = "O código da unidade é obrigatório.")
    private final Long codUnidade;
    @ApiModelProperty(
            value = "Nome da unidade.",
            example = "Unidade de testes")
    @NotBlank(message = "O nome da unidade não pode estar vazio.")
    @Size(max = 40, message = "O nome da unidade pode conter no máximo 40 caracteres.")
    private final String nomeUnidade;
    @ApiModelProperty(
            value = "Um código auxiliar para a unidade.",
            example = "01:01",
            notes = "O código auxiliar é normalmente utilizado para integrações com sistemas externos.")
    @Nullable
    private final String codAuxiliarUnidade;
    @ApiModelProperty(
            value = "A latitude da localização da unidade.",
            example = "-27.641369")
    @Nullable
    private final String latitudeUnidade;
    @ApiModelProperty(
            value = "A latitude da localização da unidade.",
            example = "-48.679233")
    @Nullable
    private final String longitudeUnidade;
}
