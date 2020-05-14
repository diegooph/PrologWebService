package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.CapturaFotoChecklistEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 2019-09-22
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class PerguntaModeloChecklistEdicaoInsere extends PerguntaModeloChecklistEdicao {
    @NotNull
    private final String descricao;
    @Nullable
    private final Long codImagem;
    private final int ordemExibicao;
    private final boolean singleChoice;
    @NotNull
    private final CapturaFotoChecklistEnum capturaFotosRespostaOk;
    @NotNull
    private final List<AlternativaModeloChecklistEdicao> alternativas;

    @NotNull
    @Override
    public Long getCodigo() {
        throw new UnsupportedOperationException(PerguntaModeloChecklistEdicaoInsere.class.getSimpleName()
                + " não tem codigo");
    }

    @NotNull
    @Override
    public Long getCodigoContexto() {
        throw new UnsupportedOperationException(PerguntaModeloChecklistEdicaoInsere.class.getSimpleName()
                + " não tem codigoContexto");
    }

    @NotNull
    @Override
    public List<AlternativaModeloChecklist> getAlternativas() {
        //noinspection unchecked
        return (List<AlternativaModeloChecklist>) (List<?>) alternativas;
    }
}
