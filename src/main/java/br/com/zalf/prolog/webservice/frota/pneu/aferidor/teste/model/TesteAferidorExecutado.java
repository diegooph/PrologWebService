package br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2019-10-07
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class TesteAferidorExecutado {
    @NotNull
    private final Long codColaboradorExecucao;
    @NotNull
    private final String dispositivo;
    @NotNull
    private final List<ComandoExecutadoTeste> comandosExecutados;

    public TesteAferidorExecutado(@NotNull final Long codColaboradorExecucao,
                                  @NotNull final String dispositivo,
                                  @NotNull final List<ComandoExecutadoTeste> comandosExecutados) {
        this.codColaboradorExecucao = codColaboradorExecucao;
        this.dispositivo = dispositivo;
        this.comandosExecutados = comandosExecutados;
    }

    @NotNull
    public Long getCodColaboradorExecucao() {
        return codColaboradorExecucao;
    }

    @NotNull
    public String getDispositivo() {
        return dispositivo;
    }

    @NotNull
    public List<ComandoExecutadoTeste> getComandosExecutados() {
        return comandosExecutados;
    }
}