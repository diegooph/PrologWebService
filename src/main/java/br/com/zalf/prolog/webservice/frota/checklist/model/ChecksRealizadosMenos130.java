package br.com.zalf.prolog.webservice.frota.checklist.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 07/11/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class ChecksRealizadosMenos130 {
    @NotNull
    private final String nomeUnidade;
    @NotNull
    private final String nomeColaborador;
    private final int qtdChecksRealizadosEmMenosDe130;
    private final int qtdChecksRealizadosUltimos30Dias;

    public ChecksRealizadosMenos130(@NotNull final String nomeUnidade,
                                    @NotNull final String nomeColaborador,
                                    final int qtdChecksRealizadosEmMenosDe130,
                                    final int qtdChecksRealizadosUltimos30Dias) {
        this.nomeUnidade = nomeUnidade;
        this.nomeColaborador = nomeColaborador;
        this.qtdChecksRealizadosEmMenosDe130 = qtdChecksRealizadosEmMenosDe130;
        this.qtdChecksRealizadosUltimos30Dias = qtdChecksRealizadosUltimos30Dias;
    }

    @NotNull
    public String getnomeUnidade() {
        return nomeUnidade;
    }

    @NotNull
    public String getNomeColaborador() {
        return nomeColaborador;
    }

    public int getQtdChecksRealizadosEmMenosDe130() {
        return qtdChecksRealizadosEmMenosDe130;
    }

    public int getQtdChecksRealizadosUltimos30Dias() {
        return qtdChecksRealizadosUltimos30Dias;
    }
}