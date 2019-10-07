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
    private final String nomeDispositivo;
    @NotNull
    private final List<ComandoExecutadoTeste> comandosExecutados;

    public TesteAferidorExecutado(@NotNull final Long codColaboradorExecucao,
                                  @NotNull final String nomeDispositivo,
                                  @NotNull final List<ComandoExecutadoTeste> comandosExecutados) {
        this.codColaboradorExecucao = codColaboradorExecucao;
        this.nomeDispositivo = nomeDispositivo;
        this.comandosExecutados = comandosExecutados;
    }

    @NotNull
    public Long getCodColaboradorExecucao() {
        return codColaboradorExecucao;
    }

    @NotNull
    public String getNomeDispositivo() {
        return nomeDispositivo;
    }

    @NotNull
    public List<ComandoExecutadoTeste> getComandosExecutados() {
        return comandosExecutados;
    }
}