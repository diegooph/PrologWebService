package br.com.zalf.prolog.webservice.gente.colaborador.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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