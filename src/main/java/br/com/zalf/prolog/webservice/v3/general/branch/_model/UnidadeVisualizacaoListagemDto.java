package br.com.zalf.prolog.webservice.v3.general.branch._model;

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
    @ApiModelProperty(value = "Código da unidade.", required = true, example = "215")
    @NotNull
    Long codUnidade;
    @ApiModelProperty(value = "Nome da unidade.", required = true, example = "Unidade de testes")
    @NotNull
    String nomeUnidade;
    @ApiModelProperty(value = "Quantidade total de colaboradores ativos cadastrados que a unidade possui. Para " +
            "algumas unidades esse valor pode ser zero.",
                      example = "70")
    int totalColaboradores;
    @ApiModelProperty(value = "Timezone configurado para a unidade.", required = true, example = "America/Sao_Paulo")
    @NotNull
    String timezoneUnidade;
    @ApiModelProperty(value = "Data e hora que a unidade foi cadastrada, em UTC.",
                      required = true,
                      example = "2019-08-18T10:47:00")
    @NotNull
    LocalDateTime dataHoraCadastroUnidade;
    @ApiModelProperty(value = "Flag que indica se a unidade está ativa.", required = true, example = "true")
    boolean unidadeAtiva;
    @ApiModelProperty(value = "Código auxiliar de uma unidade.",
                      notes = "O código auxiliar é normalmente utilizado para integrações com sistemas externos.",
                      example = "01:01")
    @Nullable
    String codAuxiliar;
    @ApiModelProperty(value = "A latitude da unidade.", example = "-27.641369")
    @Nullable
    String latitudeUnidade;
    @ApiModelProperty(value = "A longitude da unidade.", example = "-48.679233")
    @Nullable
    String longitudeUnidade;
    @ApiModelProperty(value = "Código do grupo da unidade.", required = true, example = "1")
    @NotNull
    Long codGrupo;
    @ApiModelProperty(value = "Nome do grupo da unidade.", required = true, example = "Sudeste")
    @NotNull
    String nomeGrupo;
}
