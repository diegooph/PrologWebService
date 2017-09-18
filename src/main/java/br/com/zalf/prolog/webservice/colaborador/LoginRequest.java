package br.com.zalf.prolog.webservice.colaborador;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public class LoginRequest {

    @NotNull
    private Long cpf;

    @Nullable
    private Long versaoDadosIntervalo;

    public LoginRequest(@NotNull final Long cpf,@Nullable final Long versaoDadosIntervalo) {
        this.cpf = cpf;
        this.versaoDadosIntervalo = versaoDadosIntervalo;
    }

    public Long getCpf() {
        return cpf;
    }

    public void setCpf(Long cpf) {
        this.cpf = cpf;
    }

    public Long getVersaoDadosIntervalo() {
        return versaoDadosIntervalo;
    }

    public void setVersaoDadosIntervalo(Long versaoDadosIntervalo) {
        this.versaoDadosIntervalo = versaoDadosIntervalo;
    }
}