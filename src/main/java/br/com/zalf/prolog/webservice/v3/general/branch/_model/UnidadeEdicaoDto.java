package br.com.zalf.prolog.webservice.v3.general.branch._model;

import br.com.zalf.prolog.webservice.v3.validation.BranchId;
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
@Builder
@Getter
public final class UnidadeEdicaoDto {
    @BranchId
    @ApiModelProperty(value = "Código da unidade.", required = true, example = "215")
    @NotNull(message = "O código da unidade é obrigatório.")
    private final Long codUnidade;
    @ApiModelProperty(value = "Nome da unidade.", required = true, example = "Unidade de testes")
    @NotBlank(message = "O nome da unidade não pode estar vazio.")
    @Size(max = 40, message = "O nome da unidade pode conter no máximo 40 caracteres.")
    private final String nomeUnidade;
    @ApiModelProperty(value = "Um código auxiliar para a unidade.",
                      notes = "O código auxiliar é normalmente utilizado para integrações com sistemas externos.",
                      example = "01:01")
    @Nullable
    private final String codAuxiliarUnidade;
    @ApiModelProperty(value = "A latitude da localização da unidade.", example = "-27.641369")
    @Nullable
    private final String latitudeUnidade;
    @ApiModelProperty(value = "A latitude da localização da unidade.", example = "-48.679233")
    @Nullable
    private final String longitudeUnidade;
}
