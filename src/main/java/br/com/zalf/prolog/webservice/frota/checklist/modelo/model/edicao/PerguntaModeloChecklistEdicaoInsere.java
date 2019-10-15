package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 2019-09-22
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PerguntaModeloChecklistEdicaoInsere extends PerguntaModeloChecklistEdicao {
    @NotNull
    private final String descricao;
    @Nullable
    private final Long codImagem;
    private final int ordemExibicao;
    private final boolean singleChoice;
    @NotNull
    private final List<AlternativaModeloChecklistEdicao> alternativas;

    public PerguntaModeloChecklistEdicaoInsere(@NotNull final String descricao,
                                               @Nullable final Long codImagem,
                                               final int ordemExibicao,
                                               final boolean singleChoice,
                                               @NotNull final List<AlternativaModeloChecklistEdicao> alternativas) {
        this.descricao = descricao;
        this.codImagem = codImagem;
        this.ordemExibicao = ordemExibicao;
        this.singleChoice = singleChoice;
        this.alternativas = alternativas;
    }

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
    public String getDescricao() {
        return descricao;
    }

    @Nullable
    @Override
    public Long getCodImagem() {
        return codImagem;
    }

    @Override
    public int getOrdemExibicao() {
        return ordemExibicao;
    }

    @Override
    public boolean isSingleChoice() {
        return singleChoice;
    }

    @NotNull
    @Override
    public List<AlternativaModeloChecklist> getAlternativas() {
        //noinspection unchecked
        return (List<AlternativaModeloChecklist>) (List<?>) alternativas;
    }
}
