package br.com.zalf.prolog.webservice.frota.checklist.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 30/04/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FarolPergunta {
    @NotNull
    private final String descricaoPergunta;
    @NotNull
    private final String respostaSelecionada;
    private final boolean respostaTipoOutros;
    @Nullable
    private final String descricaoRespostaTipoOutros;
    @NotNull
    private final List<FarolItemCritico> itensCriticosEmAberto;

    public FarolPergunta(@NotNull final String descricaoPergunta,
                         @NotNull final String respostaSelecionada,
                         final boolean respostaTipoOutros,
                         @NotNull final String descricaoRespostaTipoOutros,
                         @Nullable final List<FarolItemCritico> itensCriticosEmAberto) {


        this.descricaoPergunta = descricaoPergunta;
        this.respostaSelecionada = respostaSelecionada;
        this.respostaTipoOutros = respostaTipoOutros;
        this.descricaoRespostaTipoOutros = descricaoRespostaTipoOutros;
        this.itensCriticosEmAberto = itensCriticosEmAberto;
    }
}