package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.CapturaFotoChecklistEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class AlternativaModeloChecklistVisualizacao extends AlternativaModeloChecklist {
    @NotNull
    private final Long codigo;
    @NotNull
    private final Long codigoContexto;
    @NotNull
    private final String descricao;
    @NotNull
    private final PrioridadeAlternativa prioridade;
    private final boolean tipoOutros;
    private final int ordemExibicao;
    private final boolean deveAbrirOrdemServico;
    @NotNull
    private final CapturaFotoChecklistEnum capturaFotos;
}
