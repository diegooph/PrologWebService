package br.com.zalf.prolog.webservice.frota.checklist.model.farol;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 30/04/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FarolItemCritico {
    @NotNull
    private final Long codigoItemCritico;
    @NotNull
    private final String respostaSelecionada;
    private final boolean respostaTipoOutros;
    @Nullable
    private final String descricaoRespostaTipoOutros;

    public FarolItemCritico(@NotNull final Long codigoItemCritico,
                            @NotNull final String respostaSelecionada,
                            @Nullable final String descricaoRespostaTipoOutros) {
        this.codigoItemCritico = codigoItemCritico;
        this.respostaSelecionada = respostaSelecionada;
        this.respostaTipoOutros = descricaoRespostaTipoOutros != null;
        this.descricaoRespostaTipoOutros = descricaoRespostaTipoOutros;
    }

    @NotNull
    public Long getCodigoItemCritico() {
        return codigoItemCritico;
    }

    @NotNull
    public String getRespostaSelecionada() {
        return respostaSelecionada;
    }

    public boolean isRespostaTipoOutros() {
        return respostaTipoOutros;
    }

    @Nullable
    public String getDescricaoRespostaTipoOutros() {
        return descricaoRespostaTipoOutros;
    }
}