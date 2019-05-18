package br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao;

import org.jetbrains.annotations.NotNull;

/**
 * Representa um {@link TipoMarcacao tipo de marcação} que deve ser descontado na hora de calcular a jornada bruta ou
 * líquida.
 *
 * Created on 01/02/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class TipoDescontadoJornada {
    @NotNull
    private final Long codTipo;
    @NotNull
    private final String nomeTipo;

    public TipoDescontadoJornada(@NotNull final Long codTipo, @NotNull final String nomeTipo) {
        this.codTipo = codTipo;
        this.nomeTipo = nomeTipo;
    }

    @NotNull
    public Long getCodTipo() {
        return codTipo;
    }

    @NotNull
    public String getNomeTipo() {
        return nomeTipo;
    }
}