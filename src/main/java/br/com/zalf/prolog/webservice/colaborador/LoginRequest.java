package br.com.zalf.prolog.webservice.colaborador;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.Date;

public class LoginRequest {

    @NotNull
    private Long cpf;

    @Nullable
    private Date dataHoraUltimaAlteracaoDadosIntervalo;

    public LoginRequest(@NotNull final Long cpf,@Nullable final Date dataHoraUltimaAlteracaoDadosIntervalo) {
        this.cpf = cpf;
        this.dataHoraUltimaAlteracaoDadosIntervalo = dataHoraUltimaAlteracaoDadosIntervalo;
    }

    public Long getCpf() {
        return cpf;
    }

    public void setCpf(Long cpf) {
        this.cpf = cpf;
    }

    public Date getDataHoraUltimaAlteracaoDadosIntervalo() {
        return dataHoraUltimaAlteracaoDadosIntervalo;
    }

    public void setDataHoraUltimaAlteracaoDadosIntervalo(Date dataHoraUltimaAlteracaoDadosIntervalo) {
        this.dataHoraUltimaAlteracaoDadosIntervalo = dataHoraUltimaAlteracaoDadosIntervalo;
    }
}