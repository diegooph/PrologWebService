package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AnexoMidiaChecklistEnum;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.PerguntaModeloChecklist;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 10/12/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class PerguntaModeloChecklistInsercao extends PerguntaModeloChecklist {
    @NotNull
    private final String descricao;
    @Nullable
    private final Long codImagem;
    private final int ordemExibicao;
    private final boolean singleChoice;
    @NotNull
    private final AnexoMidiaChecklistEnum anexoMidiaRespostaOk;
    @NotNull
    private final List<AlternativaModeloChecklistInsercao> alternativas;

    @NotNull
    @Override
    public Long getCodigo() {
        throw new UnsupportedOperationException("Pergunta de inserção não possui código!");
    }

    @NotNull
    @Override
    public Long getCodigoContexto() {
        throw new UnsupportedOperationException("Pergunta de inserção não possui código de contexto!");
    }

    @Nullable
    @Override
    public String getUrlImagem() {
        return null;
    }

    @NotNull
    @Override
    public List<AlternativaModeloChecklist> getAlternativas() {
        //noinspection unchecked
        return (List<AlternativaModeloChecklist>) (List<?>) alternativas;
    }
}