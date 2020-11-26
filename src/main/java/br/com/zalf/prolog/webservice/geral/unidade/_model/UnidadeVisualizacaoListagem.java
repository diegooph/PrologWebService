package br.com.zalf.prolog.webservice.geral.unidade._model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@ApiModel(description = "Objeto com as informações de uma unidade.")
@Value
public class UnidadeVisualizacaoListagem {
    @ApiModelProperty(
            value = "O código único de uma unidade.",
            example = "215")
    @NotNull
    Long codUnidade;
    @ApiModelProperty(
            value = "O nome de uma unidade.",
            example = "Unidade de testes")
    @NotNull
    String nomeUnidade;
    @ApiModelProperty(
            value = "A quantidade total de colaboradores ativos cadastrados que uma unidade possui.",
            example = "7")
    int totalColaboradores;
    @ApiModelProperty(
            value = "O código único da regional de uma unidade.",
            example = "1")
    @NotNull
    Long codRegional;
    @ApiModelProperty(
            value = "O nome da regional de uma unidade.",
            example = "Sudeste")
    @NotNull
    String nomeRegional;
    @ApiModelProperty(
            value = "O timezone de uma unidade.",
            example = "America/Sao_Paulo")
    @NotNull
    String timezoneUnidade;
    @ApiModelProperty(
            value = "A data e hora de cadastro de uma unidade, em UTC.",
            example = "2019-08-18T10:47:00")
    @NotNull
    LocalDateTime dataHoraCadastroUnidade;
    @ApiModelProperty(
            value = "Um indicativo de se a unidade está ou não ativa.",
            example = "true")
    boolean unidadeAtiva;
    @ApiModelProperty(
            value = "O código auxiliar de uma unidade.",
            example = "01:01")
    @Nullable
    String codAuxiliar;
    @ApiModelProperty(
            value = "A latitude de uma unidade.",
            example = "-27.641369")
    @Nullable
    String latitudeUnidade;
    @ApiModelProperty(
            value = "A longitude de uma unidade.",
            example = "-48.679233")
    @Nullable
    String longitudeUnidade;
}
