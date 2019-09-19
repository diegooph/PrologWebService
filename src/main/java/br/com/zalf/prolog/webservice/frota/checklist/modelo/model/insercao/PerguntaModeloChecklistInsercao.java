package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 10/12/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PerguntaModeloChecklistInsercao {
    @NotNull
    private final String descricao;
    @Nullable
    private final Long codImagem;
    private final int ordemExibicao;
    private final boolean singleChoice;
    @NotNull
    private final List<AlternativaModeloChecklistInsercao> alternativas;


    public PerguntaModeloChecklistInsercao(@NotNull final String descricao,
                                           @Nullable final Long codImagem,
                                           final int ordemExibicao,
                                           final boolean singleChoice,
                                           @NotNull final List<AlternativaModeloChecklistInsercao> alternativas) {
        this.descricao = descricao;
        this.codImagem = codImagem;
        this.ordemExibicao = ordemExibicao;
        this.singleChoice = singleChoice;
        this.alternativas = alternativas;
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
    public List<AlternativaModeloChecklistInsercao> getAlternativas() {
        return alternativas;
    }
}