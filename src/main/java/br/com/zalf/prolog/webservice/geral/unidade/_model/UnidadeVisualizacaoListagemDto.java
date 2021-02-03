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
public class UnidadeVisualizacaoListagemDto {
    @ApiModelProperty(
            value = "Código único da unidade.",
            example = "215")
    @NotNull
    Long codUnidade;
    @ApiModelProperty(
            value = "Nome da unidade.",
            example = "Unidade de testes")
    @NotNull
    String nomeUnidade;
    @ApiModelProperty(
            value = "Quantidade total de colaboradores ativos cadastrados que a unidade possui.",
            example = "70")
    int totalColaboradores;
    @ApiModelProperty(
            value = "Timezone configurado para a unidade.",
            example = "America/Sao_Paulo")
    @NotNull
    String timezoneUnidade;
    @ApiModelProperty(
            value = "Data e hora que a unidade foi cadastrada, em UTC.",
            example = "2019-08-18T10:47:00")
    @NotNull
    LocalDateTime dataHoraCadastroUnidade;
    @ApiModelProperty(
            value = "Indicativo de se a unidade está ou não ativa.",
            example = "true")
    boolean unidadeAtiva;
    @ApiModelProperty(
            value = "Código auxiliar de uma unidade.",
            example = "01:01",
            notes = "O código auxiliar é normalmente utilizado para integrações com sistemas externos.")
    @Nullable
    String codAuxiliar;
    @ApiModelProperty(
            value = "A latitude da unidade.",
            example = "-27.641369")
    @Nullable
    String latitudeUnidade;
    @ApiModelProperty(
            value = "A longitude da unidade.",
            example = "-48.679233")
    @Nullable
    String longitudeUnidade;
    @ApiModelProperty(
            value = "Código único da regional da unidade.",
            example = "1")
    @NotNull
    Long codRegional;
    @ApiModelProperty(
            value = "Nome da regional da unidade.",
            example = "Sudeste")
    @NotNull
    String nomeRegional;
}
