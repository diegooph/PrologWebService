package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 3/11/20
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ResponseAfericaoProtheusNepomuceno {
    /**
     * Valor numérico que representa o código da aferição que acabou de ser inserida.
     */
    @NotNull
    private final Long codigoAfericaoInserida;
    /**
     * Valor alfanumérico que contém uma descrição sobre o processo de inserção da aferição.
     */
    @NotNull
    private final String msg;

    public ResponseAfericaoProtheusNepomuceno(@NotNull final Long codigoAfericaoInserida,
                                              @NotNull final String msg) {
        this.codigoAfericaoInserida = codigoAfericaoInserida;
        this.msg = msg;
    }

    @NotNull
    public Long getCodigoAfericaoInserida() {
        return codigoAfericaoInserida;
    }

    @NotNull
    public String getMsg() {
        return msg;
    }
}
