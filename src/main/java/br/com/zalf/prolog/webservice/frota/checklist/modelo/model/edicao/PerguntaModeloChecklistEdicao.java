package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class PerguntaModeloChecklistEdicao {
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
    private final List<AlternativaModeloChecklist> alternativas;

    public PerguntaModeloChecklistEdicao(@NotNull final Long codigo,
                                         @NotNull final Long codigoFixo,
                                         @NotNull final String descricao,
                                         @Nullable final Long codImagem,
                                         final int ordemExibicao,
                                         final boolean singleChoice,
                                         @NotNull final List<AlternativaModeloChecklist> alternativas) {
        this.codigo = codigo;
        this.codigoFixo = codigoFixo;
        this.descricao = descricao;
        this.codImagem = codImagem;
        this.ordemExibicao = ordemExibicao;
        this.singleChoice = singleChoice;
        this.alternativas = alternativas;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public Long getCodigoFixo() {
        return codigoFixo;
    }

    @NotNull
    public String getDescricao() {
        return descricao;
    }

    @Nullable
    public Long getCodImagem() {
        return codImagem;
    }

    public int getOrdemExibicao() {
        return ordemExibicao;
    }

    public boolean isSingleChoice() {
        return singleChoice;
    }

    @NotNull
    public List<AlternativaModeloChecklist> getAlternativas() {
        return alternativas;
    }
}
