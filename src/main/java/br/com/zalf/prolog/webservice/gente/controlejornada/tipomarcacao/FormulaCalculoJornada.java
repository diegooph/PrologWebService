package br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 31/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FormulaCalculoJornada {
    @NotNull
    private final List<TipoDescontadoJornada> tiposDescontadosJornadaBruta;
    @NotNull
    private final List<TipoDescontadoJornada> tiposDescontadosJornadaLiquida;
    @NotNull
    private final String formulaCalculoJornadaBruta;
    @NotNull
    private final String formulaCalculoJornadaLiquida;

    public FormulaCalculoJornada(@NotNull final List<TipoDescontadoJornada> tiposDescontadosJornadaBruta,
                                 @NotNull final List<TipoDescontadoJornada> tiposDescontadosJornadaLiquida) {
        this.tiposDescontadosJornadaBruta = tiposDescontadosJornadaBruta;
        this.tiposDescontadosJornadaLiquida = tiposDescontadosJornadaLiquida;
        this.formulaCalculoJornadaBruta = createFormulaCalculoJornadaBruta();
        this.formulaCalculoJornadaLiquida = createFormulaCalculoJornadaLiquida();
    }

    @NotNull
    public List<TipoDescontadoJornada> getTiposDescontadosJornadaBruta() {
        return tiposDescontadosJornadaBruta;
    }

    @NotNull
    public List<TipoDescontadoJornada> getTiposDescontadosJornadaLiquida() {
        return tiposDescontadosJornadaLiquida;
    }

    @NotNull
    public String getFormulaCalculoJornadaBruta() {
        return formulaCalculoJornadaBruta;
    }

    @NotNull
    public String getFormulaCalculoJornadaLiquida() {
        return formulaCalculoJornadaLiquida;
    }

    @NotNull
    private String createFormulaCalculoJornadaBruta() {
        final StringBuilder builder = new StringBuilder("Jornada Bruta = Duração Total Jornada");
        for (final TipoDescontadoJornada tipoDescontadoJornada : tiposDescontadosJornadaBruta) {
            builder.append(" - ").append(tipoDescontadoJornada.getNomeTipo());
        }
        return builder.toString();
    }

    @NotNull
    private String createFormulaCalculoJornadaLiquida() {
        final StringBuilder builder = new StringBuilder("Jornada Líquida = Jornada Bruta");
        for (final TipoDescontadoJornada tipoDescontadoJornada : tiposDescontadosJornadaLiquida) {
            builder.append(" - ").append(tipoDescontadoJornada.getNomeTipo());
        }
        return builder.toString();
    }
}