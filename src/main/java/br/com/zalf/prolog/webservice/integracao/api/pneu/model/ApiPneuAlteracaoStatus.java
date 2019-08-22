package br.com.zalf.prolog.webservice.integracao.api.pneu.model;

import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiStatusPneu;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 21/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public abstract class ApiPneuAlteracaoStatus {
    @NotNull
    private final ApiStatusPneu statusPneu;
    @NotNull
    private final Long codigoPneuSistemaIntegrado;
    @NotNull
    private final String codigoPneuCliente;
    @NotNull
    private final Long codUnidadePneu;
    @NotNull
    private final String cpfColaboradorAlteracaoStatus;
    @NotNull
    private final LocalDateTime dataHoraAlteracaoStatus;

    public ApiPneuAlteracaoStatus(@NotNull final ApiStatusPneu statusPneu,
                                  @NotNull final Long codigoPneuSistemaIntegrado,
                                  @NotNull final String codigoPneuCliente,
                                  @NotNull final Long codUnidadePneu,
                                  @NotNull final String cpfColaboradorAlteracaoStatus,
                                  @NotNull final LocalDateTime dataHoraAlteracaoStatus) {
        this.codigoPneuSistemaIntegrado = codigoPneuSistemaIntegrado;
        this.codigoPneuCliente = codigoPneuCliente;
        this.codUnidadePneu = codUnidadePneu;
        this.cpfColaboradorAlteracaoStatus = cpfColaboradorAlteracaoStatus;
        this.dataHoraAlteracaoStatus = dataHoraAlteracaoStatus;
        this.statusPneu = statusPneu;
    }

    @NotNull
    public ApiStatusPneu getStatusPneu() {
        return statusPneu;
    }

    @NotNull
    public Long getCodigoPneuSistemaIntegrado() {
        return codigoPneuSistemaIntegrado;
    }

    @NotNull
    public String getCodigoPneuCliente() {
        return codigoPneuCliente;
    }

    @NotNull
    public Long getCodUnidadePneu() {
        return codUnidadePneu;
    }

    @NotNull
    public String getCpfColaboradorAlteracaoStatus() {
        return cpfColaboradorAlteracaoStatus;
    }

    @NotNull
    public LocalDateTime getDataHoraAlteracaoStatus() {
        return dataHoraAlteracaoStatus;
    }
}
