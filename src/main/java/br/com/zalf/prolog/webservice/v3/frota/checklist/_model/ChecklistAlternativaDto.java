package br.com.zalf.prolog.webservice.v3.frota.checklist._model;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AnexoMidiaChecklistEnum;
import io.swagger.annotations.ApiModel;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2021-04-22
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@ApiModel(description = "Objeto com as informações de um checklist.")
@Value(staticConstructor = "of")
public class ChecklistAlternativaDto {
    @NotNull
    Long codAlternativa;
    @NotNull
    Long codContextoAlternativa;
    @NotNull
    String descricaoAlternativa;
    int ordemAlternativa;
    @NotNull
    PrioridadeAlternativa prioridadeAlternativa;
    boolean alternativaTipoOutros;
    boolean deveAbrirOrdemServico;
    @NotNull
    AnexoMidiaChecklistEnum anexoMidiaAlternativaNok;
    @Nullable
    String codAuxiliarAlternativa;
    boolean isAlternativaSelecionada;
    @Nullable
    String respostaTipoOutros;
}
