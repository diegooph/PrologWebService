package br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 31/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class TipoDescontadoJornada {
    @NotNull
    private final List<Long> codTiposDescontadosJornadaBruta;
    @NotNull
    private final List<Long> codTiposDescontadosJornadaLiquida;

    public TipoDescontadoJornada(@NotNull final List<Long> codTiposDescontadosJornadaBruta,
                                 @NotNull final List<Long> codTiposDescontadosJornadaLiquida) {
        this.codTiposDescontadosJornadaBruta = codTiposDescontadosJornadaBruta;
        this.codTiposDescontadosJornadaLiquida = codTiposDescontadosJornadaLiquida;
    }

    @NotNull
    public List<Long> getCodTiposDescontadosJornadaBruta() {
        return codTiposDescontadosJornadaBruta;
    }

    @NotNull
    public List<Long> getCodTiposDescontadosJornadaLiquida() {
        return codTiposDescontadosJornadaLiquida;
    }
}