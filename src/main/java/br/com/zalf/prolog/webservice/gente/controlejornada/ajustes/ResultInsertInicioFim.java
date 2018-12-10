package br.com.zalf.prolog.webservice.gente.controlejornada.ajustes;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 28/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class ResultInsertInicioFim {
    @NotNull
    private final Long codMarcacaoInicio;
    @NotNull
    private final Long codMarcacaoFim;

    ResultInsertInicioFim(@NotNull final Long codMarcacaoInicio, @NotNull final Long codMarcacaoFim) {
        this.codMarcacaoInicio = codMarcacaoInicio;
        this.codMarcacaoFim = codMarcacaoFim;
    }

    @NotNull
    Long getCodMarcacaoInicio() {
        return codMarcacaoInicio;
    }

    @NotNull
    Long getCodMarcacaoFim() {
        return codMarcacaoFim;
    }
}