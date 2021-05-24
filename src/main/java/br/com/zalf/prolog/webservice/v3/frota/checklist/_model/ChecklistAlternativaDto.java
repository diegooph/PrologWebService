package br.com.zalf.prolog.webservice.v3.frota.checklist._model;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AnexoMidiaChecklistEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2021-04-22
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Value(staticConstructor = "of")
public class ChecklistAlternativaDto {
    @ApiModelProperty(value = "Código da alternativa", required = true, example = "12345")
    @NotNull
    Long codAlternativa;
    @ApiModelProperty(value = "Código de contexto da alternativa. Esse código só será alterado caso a alternativa " +
            "sofra alterações que mudem seu contexto.",
                      required = true,
                      example = "12345")
    @NotNull
    Long codContextoAlternativa;
    @ApiModelProperty(value = "Descrição da alternativa", required = true, example = "Sem freio")
    @NotNull
    String descricaoAlternativa;
    @ApiModelProperty(value = "Ordem da alternativa. Utilizado para mostrar ao colaborador no momento da realização.",
                      required = true,
                      example = "1")
    int ordemAlternativa;
    @ApiModelProperty(value = "Prioridade da alternativa. Podendo ser CRITICA, ALTA ou BAIXA.",
                      required = true,
                      example = "CRITICA")
    @NotNull
    PrioridadeAlternativa prioridadeAlternativa;
    @ApiModelProperty(value = "Flag indicando se a alternativa é 'Outros'. Alternativa Outros exigem que o " +
            "colaborador insira um texto descrevendo o problema encontrado.",
                      required = true,
                      example = "true")
    boolean alternativaTipoOutros;
    @ApiModelProperty(value = "Flag indicando se a alternativa deve abrir Orden de Serviço.",
                      required = true,
                      example = "true")
    boolean deveAbrirOrdemServico;
    @ApiModelProperty(value = "Opção de captura de mídia caso marcar a alternativa como NOK. Os valores podem ser " +
            "BLOQUEADO, caso não deve inserir midia, OBRIGATORIO caso for obrigatório ou OPCIONAL onde o colaborador " +
            "opta por inserir uma mídia ou não.",
                      required = true,
                      example = "BLOQUEADO")
    @NotNull
    AnexoMidiaChecklistEnum anexoMidiaAlternativaNok;
    @ApiModelProperty(value = "Código auxiliar. Código presente nas alternativas, utilizado em cenários de integração",
                      example = "Serviço=FREIO")
    @Nullable
    String codAuxiliarAlternativa;
    @ApiModelProperty(value = "Flag indicando se a alternativa em questão foi marcada como NOK pelo colaborador.",
                      required = true,
                      example = "true")
    boolean isAlternativaSelecionada;
    @ApiModelProperty(value = "Resposta textual inserida pelo colaborador para descrever o problema encontrado.",
                      example = "Problema no líquido de freio")
    @Nullable
    String respostaTipoOutros;
}
