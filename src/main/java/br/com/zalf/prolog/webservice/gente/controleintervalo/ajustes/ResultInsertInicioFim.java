package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 28/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ResultInsertInicioFim {
    @NotNull
    private final Long codMarcacaoInicio;
    @NotNull
    private final Long codMarcacaoFim;

    public ResultInsertInicioFim(@NotNull final Long codMarcacaoInicio, @NotNull final Long codMarcacaoFim) {
        this.codMarcacaoInicio = codMarcacaoInicio;
        this.codMarcacaoFim = codMarcacaoFim;
    }

    @NotNull
    public Long getCodMarcacaoInicio() {
        return codMarcacaoInicio;
    }

    @NotNull
    public Long getCodMarcacaoFim() {
        return codMarcacaoFim;
    }
}