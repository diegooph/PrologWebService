package br.com.zalf.prolog.webservice.integracao.response;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 08/01/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class SuccessResponseIntegracao {
    @NotNull
    private String msg;
    @Nullable
    private Long codigo;

    public SuccessResponseIntegracao(@NotNull final String msg) {
        this.msg = msg;
    }

    public SuccessResponseIntegracao(@NotNull final String msg, @Nullable final Long codigo) {
        this.msg = msg;
        this.codigo = codigo;
    }

    @NotNull
    public String getMsg() {
        return msg;
    }

    public void setMsg(@NotNull final String msg) {
        this.msg = msg;
    }

    @Nullable
    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(@Nullable final Long codigo) {
        this.codigo = codigo;
    }
}
