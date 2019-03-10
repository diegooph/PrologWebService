package br.com.zalf.prolog.webservice.frota.checklist.offline;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PerguntaModeloChecklistOffline {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String descricao;
    @NotNull
    private final Long codImagem;
    @NotNull
    private final String urlImagem;
    private final int ordemExibicao;
    private final boolean singleChoice;
    @NotNull
    private final List<AlternativaModeloChecklistOffline> alternativas;

    public PerguntaModeloChecklistOffline(@NotNull final Long codigo,
                                          @NotNull final String descricao,
                                          @NotNull final Long codImagem,
                                          @NotNull final String urlImagem,
                                          final int ordemExibicao,
                                          final boolean singleChoice,
                                          @NotNull final List<AlternativaModeloChecklistOffline> alternativas) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.codImagem = codImagem;
        this.urlImagem = urlImagem;
        this.ordemExibicao = ordemExibicao;
        this.singleChoice = singleChoice;
        this.alternativas = alternativas;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getDescricao() {
        return descricao;
    }

    @NotNull
    public Long getCodImagem() {
        return codImagem;
    }

    @NotNull
    public String getUrlImagem() {
        return urlImagem;
    }

    @NotNull
    public int getOrdemExibicao() {
        return ordemExibicao;
    }

    @NotNull
    public boolean isSingleChoice() {
        return singleChoice;
    }

    @NotNull
    public List<AlternativaModeloChecklistOffline> getAlternativas() {
        return alternativas;
    }
}
