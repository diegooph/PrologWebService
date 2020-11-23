package br.com.zalf.prolog.webservice.geral.unidade._model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@ApiModel(description = "Objeto com as informações de uma unidade.")
@Data
public final class UnidadeVisualizacaoListagem {
    @ApiModelProperty(
            value = "O código único de uma unidade.",
            example = "215"
    )
    @NotNull
    public final Long codUnidade;
    @ApiModelProperty(
            value = "O nome de uma unidade.",
            example = "Unidade de testes"
    )
    @NotNull
    public final String nomeUnidade;
    @ApiModelProperty(
            value = "A quantidade total de colaboradores ativos cadastrados que uma unidade possui.",
            example = "7"
    )
    public final int totalColaboradores;
    @ApiModelProperty(
            value = "O código único da regional de uma unidade.",
            example = "1"
    )
    @NotNull
    public final Long codRegional;
    @ApiModelProperty(
            value = "O nome da regional de uma unidade.",
            example = "Sudeste"
    )
    @NotNull
    public final String nomeRegional;
    @ApiModelProperty(
            value = "O timezone de uma unidade.",
            example = "America/Sao_Paulo"
    )
    @NotNull
    public final String timezoneUnidade;
    @ApiModelProperty(
            value = "A data e hora de cadastro de uma unidade, em UTC.",
            example = "2019-08-18T10:47:00"
    )
    @NotNull
    public final LocalDateTime dataHoraCadastroUnidade;
    @ApiModelProperty(
            value = "Um indicativo de se a unidade está ou não ativa.",
            example = "true"
    )
    public final boolean unidadeAtiva;
    @ApiModelProperty(
            value = "O código auxiliar de uma unidade.",
            example = "01:01"
    )
    @Nullable
    public final String codAuxiliar;
    @ApiModelProperty(
            value = "A latitude de uma unidade.",
            example = "-27.641369"
    )
    @Nullable
    public final String latitudeUnidade;
    @ApiModelProperty(
            value = "A longitude de uma unidade.",
            example = "-48.679233"
    )
    @Nullable
    public final String longitudeUnidade;
}
