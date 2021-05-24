package br.com.zalf.prolog.webservice.v3.frota.checklist._model;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AnexoMidiaChecklistEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2021-04-22
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Value(staticConstructor = "of")
public class ChecklistPerguntasDto {
    @ApiModelProperty(value = "Código da pergunta", required = true, example = "12345")
    @NotNull
    Long codPergunta;
    @ApiModelProperty(value = "Código de contexto da pergunta. Esse código só será alterado caso a pergunta sofra " +
            "alterações que mudem seu contexto.",
                      required = true,
                      example = "12345")
    @NotNull
    Long codContextoPergunta;
    @ApiModelProperty(value = "Descrição da pergunta", required = true, example = "Freios")
    @NotNull
    String descricaoPergunta;
    @ApiModelProperty(value = "Ordem da pergunta. Utilizado para mostrar ao colaborador no momento da realização.",
                      required = true,
                      example = "1")
    int ordemPergunta;
    @ApiModelProperty(value = "Flag que indica se o colaborador pode selecionar uma ou várias alternativas.",
                      required = true,
                      example = "true")
    boolean isPerguntaSingleChoice;
    @ApiModelProperty(value = "Opção de captura de mídia caso marcar a pergunta como OK. Os valores podem ser " +
            "BLOQUEADO, caso não deve inserir midia, OBRIGATORIO caso for obrigatório ou OPCIONAL onde o colaborador " +
            "opta por inserir uma mídia ou não.",
                      required = true,
                      example = "BLOQUEADO")
    @NotNull
    AnexoMidiaChecklistEnum anexoMidiaPerguntaOk;
    @ApiModelProperty(value = "Alternativas da pergunta. A pergunta têm no mínimo uma alternativa.")
    @NotNull
    List<ChecklistAlternativaDto> alternativaPergunta;
}
