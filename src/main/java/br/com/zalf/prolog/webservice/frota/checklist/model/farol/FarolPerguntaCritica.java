package br.com.zalf.prolog.webservice.frota.checklist.model.farol;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 30/04/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FarolPerguntaCritica {
    @NotNull
    private final Long codigoPergunta;
    @NotNull
    private final String descricaoPergunta;
    @NotNull
    private final List<FarolItemCritico> itensCriticosEmAberto;

    public FarolPerguntaCritica(@NotNull final Long codigoPergunta,
                                @NotNull final String descricaoPergunta,
                                @NotNull final List<FarolItemCritico> itensCriticosEmAberto) {
        this.codigoPergunta = codigoPergunta;
        this.descricaoPergunta = descricaoPergunta;
        this.itensCriticosEmAberto = itensCriticosEmAberto;
    }

    @NotNull
    public Long getCodigoPergunta() {
        return codigoPergunta;
    }

    @NotNull
    public String getDescricaoPergunta() {
        return descricaoPergunta;
    }

    @NotNull
    public List<FarolItemCritico> getItensCriticosEmAberto() {
        return itensCriticosEmAberto;
    }
}