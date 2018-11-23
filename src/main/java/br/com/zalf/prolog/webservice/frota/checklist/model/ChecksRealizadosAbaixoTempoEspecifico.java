package br.com.zalf.prolog.webservice.frota.checklist.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 07/11/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class ChecksRealizadosAbaixoTempoEspecifico {
    @NotNull
    private final String nomeUnidade;
    @NotNull
    private final String nomeColaborador;
    private final int qtdChecksRealizadosAbaixoTempoEspecifico;
    private final int qtdChecksRealizados;

    public ChecksRealizadosAbaixoTempoEspecifico(@NotNull final String nomeUnidade,
                                                 @NotNull final String nomeColaborador,
                                                 final int qtdChecksRealizadosAbaixoTempoEspecifico,
                                                 final int qtdChecksRealizados) {
        this.nomeUnidade = nomeUnidade;
        this.nomeColaborador = nomeColaborador;
        this.qtdChecksRealizadosAbaixoTempoEspecifico = qtdChecksRealizadosAbaixoTempoEspecifico;
        this.qtdChecksRealizados = qtdChecksRealizados;
    }

    @NotNull
    public String getNomeUnidade() {
        return nomeUnidade;
    }

    @NotNull
    public String getNomeColaborador() {
        return nomeColaborador;
    }

    public int getQtdChecksRealizadosAbaixoTempoEspecifico() {
        return qtdChecksRealizadosAbaixoTempoEspecifico;
    }

    public int getQtdChecksRealizados() {
        return qtdChecksRealizados;
    }
}