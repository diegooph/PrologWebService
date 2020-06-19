package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AnexoMidiaChecklistEnum;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.PerguntaModeloChecklist;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class PerguntaModeloChecklistVisualizacao extends PerguntaModeloChecklist {
    @NotNull
    private final Long codigo;
    @NotNull
    private final Long codigoContexto;
    @NotNull
    private final String descricao;
    @Nullable
    private final Long codImagem;
    @Nullable
    private final String urlImagem;
    private final int ordemExibicao;
    private final boolean singleChoice;
    @NotNull
    private final AnexoMidiaChecklistEnum anexoMidiaRespostaOk;
    @NotNull
    private final List<AlternativaModeloChecklistVisualizacao> alternativas;

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public List<AlternativaModeloChecklist> getAlternativas() {
        return (List<AlternativaModeloChecklist>) (List<?>) alternativas;
    }
}