package br.com.zalf.prolog.webservice.frota.checklist.model.insercao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 08/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ChecklistAlternativaResposta {
    @NotNull
    private final Long codAlternativa;
    private final boolean alternativaSelecionada;
    private final boolean tipoOutros;
    @Nullable
    private final String respostaTipoOutros;


    public ChecklistAlternativaResposta(@NotNull final Long codAlternativa,
                                        final boolean alternativaSelecionada,
                                        final boolean tipoOutros,
                                        @Nullable final String respostaTipoOutros) {
        this.codAlternativa = codAlternativa;
        this.alternativaSelecionada = alternativaSelecionada;
        this.tipoOutros = tipoOutros;
        this.respostaTipoOutros = respostaTipoOutros;
    }

    @NotNull
    public Long getCodAlternativa() {
        return codAlternativa;
    }

    public boolean isAlternativaSelecionada() {
        return alternativaSelecionada;
    }

    public boolean isTipoOutros() {
        return tipoOutros;
    }

    @Nullable
    public String getRespostaTipoOutros() {
        return respostaTipoOutros;
    }
}