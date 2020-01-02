package br.com.zalf.prolog.webservice.frota.pneu.aferidor.teste._model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2019-10-07
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ProcedimentoTesteAferidor {
    @NotNull
    private final List<String> comandosExecucao;

    public ProcedimentoTesteAferidor(@NotNull final List<String> comandosExecucao) {
        this.comandosExecucao = comandosExecucao;
    }

    @NotNull
    public List<String> getComandosExecucao() {
        return comandosExecucao;
    }
}