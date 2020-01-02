package br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Created on 2019-10-07
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ComandoExecutadoTeste {
    @NotNull
    private final String comandoEnviado;
    @Nullable
    private final String valorRecebido;

    public ComandoExecutadoTeste(@NotNull final String comandoEnviado,
                                 @Nullable final String valorRecebido) {
        this.comandoEnviado = comandoEnviado;
        this.valorRecebido = valorRecebido;
    }

    @NotNull
    public String getComandoEnviado() {
        return comandoEnviado;
    }

    @Nullable
    public String getValorRecebido() {
        return valorRecebido;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ComandoExecutadoTeste that = (ComandoExecutadoTeste) o;
        return comandoEnviado.equals(that.comandoEnviado) &&
                Objects.equals(valorRecebido, that.valorRecebido);
    }

    @Override
    public int hashCode() {
        return Objects.hash(comandoEnviado, valorRecebido);
    }
}