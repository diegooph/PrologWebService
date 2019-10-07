package br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
}