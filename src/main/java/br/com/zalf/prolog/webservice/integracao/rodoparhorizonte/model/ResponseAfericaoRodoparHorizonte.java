package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 25/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ResponseAfericaoRodoparHorizonte {
    /**
     * Valor numérico que representa o código da aferição que acabou de ser inserida.
     * <p>
     * Se a operação falhar esse valor será <code>NULL</code>.
     */
    @Nullable
    private Long codigoAfericaoInserida;
    /**
     * Valor alfanumérico que contém uma descrição sobre o processo de inserção da aferição.
     */
    @NotNull
    private final String msg;
    /**
     * Valor booleano para representar o sucesso da operação, caso <code>TRUE</code>, ou a falha da operação,
     * caso <code>FALSE</code>.
     */
    private final boolean status;

    public ResponseAfericaoRodoparHorizonte(@Nullable final Long codigoAfericaoInserida,
                                            @NotNull final String msg,
                                            final boolean status) {
        this.codigoAfericaoInserida = codigoAfericaoInserida;
        this.msg = msg;
        this.status = status;
    }

    @Nullable
    public Long getCodigoAfericaoInserida() {
        return codigoAfericaoInserida;
    }

    public void setCodigoAfericaoInserida(@Nullable final Long codigoAfericaoInserida) {
        this.codigoAfericaoInserida = codigoAfericaoInserida;
    }

    @NotNull
    public String getMsg() {
        return msg;
    }

    public boolean isStatus() {
        return status;
    }
}
