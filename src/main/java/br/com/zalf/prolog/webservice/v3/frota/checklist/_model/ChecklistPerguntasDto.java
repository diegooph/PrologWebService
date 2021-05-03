package br.com.zalf.prolog.webservice.v3.frota.checklist._model;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AnexoMidiaChecklistEnum;
import io.swagger.annotations.ApiModel;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2021-04-22
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@ApiModel(description = "Objeto com as informações de um checklist.")
@Value(staticConstructor = "of")
public class ChecklistPerguntasDto {
    @NotNull
    Long codPergunta;
    @NotNull
    Long codContextoPergunta;
    @NotNull
    String descricaoPergunta;
    int ordemPergunta;
    boolean isPerguntaSingleChoice;
    @NotNull
    AnexoMidiaChecklistEnum anexoMidiaPerguntaOk;
    @NotNull
    List<ChecklistAlternativaDto> alternativaPergunta;
}
