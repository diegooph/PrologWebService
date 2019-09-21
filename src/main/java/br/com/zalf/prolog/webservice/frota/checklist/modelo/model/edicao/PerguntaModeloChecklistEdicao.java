package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.PerguntaModeloChecklist;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class PerguntaModeloChecklistEdicao extends PerguntaModeloChecklist {
    @NotNull
    private final Long codigo;
    @NotNull
    private final Long codigoFixo;
    @NotNull
    private final String descricao;
    @Nullable
    private final Long codImagem;
    private final int ordemExibicao;
    private final boolean singleChoice;
    @NotNull
    private final List<AlternativaModeloChecklistEdicao> alternativas;

    public PerguntaModeloChecklistEdicao(@NotNull final Long codigo,
                                         @NotNull final Long codigoFixo,
                                         @NotNull final String descricao,
                                         @Nullable final Long codImagem,
                                         final int ordemExibicao,
                                         final boolean singleChoice,
                                         @NotNull final List<AlternativaModeloChecklistEdicao> alternativas) {
        this.codigo = codigo;
        this.codigoFixo = codigoFixo;
        this.descricao = descricao;
        this.codImagem = codImagem;
        this.ordemExibicao = ordemExibicao;
        this.singleChoice = singleChoice;
        this.alternativas = alternativas;
    }

    @NotNull
    @Override
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    @Override
    public Long getCodigoFixo() {
        return codigoFixo;
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

    @Nullable
    @Override
    public String getUrlImagem() {
        throw new UnsupportedOperationException(PerguntaModeloChecklistEdicao.class.getSimpleName() + " não tem UrlImagem");
    }

    @Override
    public int getOrdemExibicao() {
        return ordemExibicao;
    }

    @Override
    public boolean isSingleChoice() {
        return singleChoice;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public List<AlternativaModeloChecklist> getAlternativas() {
        return (List<AlternativaModeloChecklist>) (List<?>) alternativas;
    }
}
