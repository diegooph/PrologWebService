package br.com.zalf.prolog.webservice.frota.checklist.model.insercao;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 08/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ChecklistResposta {
    @NotNull
    private final Long codPergunta;
    @NotNull
    private final List<ChecklistAlternativaResposta> alternativasRespostas;

    public ChecklistResposta(@NotNull final Long codPergunta,
                             @NotNull final List<ChecklistAlternativaResposta> alternativasRespostas) {
        this.codPergunta = codPergunta;
        this.alternativasRespostas = alternativasRespostas;
    }

    @NotNull
    public Long getCodPergunta() {
        return codPergunta;
    }

    @NotNull
    public List<ChecklistAlternativaResposta> getAlternativasRespostas() {
        return alternativasRespostas;
    }
}