package br.com.zalf.prolog.webservice.geral.unidade._model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@ApiModel(value = "Unidade", description = "Dados a serem atualizados em uma unidade")
@Data
public final class UnidadeEdicao {

    @ApiModelProperty(
            value = "Um código de unidade, obrigatório",
            name = "codUnidade",
            dataType = "Long",
            example = "215"
    )
    @NotNull(message = "O código da unidade é obrigatório.")
    private final Long codUnidade;
    @NotNull(message = "O nome da unidade não pode estar vazio.")
    @Size(max = 40, message = "O nome da unidade precisa conter até 40 caracteres.")
    private final String nomeUnidade;
    @Nullable
    private final String codAuxiliarUnidade;
    @Nullable
    private final String latitudeUnidade;
    @Nullable
    private final String longitudeUnidade;
}
