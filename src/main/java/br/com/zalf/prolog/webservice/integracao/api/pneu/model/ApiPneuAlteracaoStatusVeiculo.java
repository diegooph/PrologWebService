package br.com.zalf.prolog.webservice.integracao.api.pneu.model;

import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiStatusPneu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created on 21/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiPneuAlteracaoStatusVeiculo extends ApiPneuAlteracaoStatus {
    @NotNull
    private String placaVeiculoPneuAplicado;
    @NotNull
    private Integer posicaoVeiculoPneuAplicado;

    public ApiPneuAlteracaoStatusVeiculo() {
        super(ApiStatusPneu.EM_USO);
    }

    public ApiPneuAlteracaoStatusVeiculo(@NotNull final Long codigoPneuSistemaIntegrado,
                                         @NotNull final String codigoPneuCliente,
                                         @NotNull final Long codUnidadePneu,
                                         @NotNull final String cpfColaboradorAlteracaoStatus,
                                         @NotNull final LocalDateTime dataHoraAlteracaoStatus,
                                         @NotNull final String placaVeiculoPneuAplicado,
                                         @NotNull final Integer posicaoVeiculoPneuAplicado,
                                         final boolean trocouDeBanda,
                                         @Nullable final Long codNovoModeloBanda,
                                         @Nullable final BigDecimal valorNovaBandaPneu) {
        super(ApiStatusPneu.EM_USO,
                codigoPneuSistemaIntegrado,
                codigoPneuCliente,
                codUnidadePneu,
                cpfColaboradorAlteracaoStatus,
                dataHoraAlteracaoStatus,
                trocouDeBanda,
                codNovoModeloBanda,
                valorNovaBandaPneu);
        this.placaVeiculoPneuAplicado = placaVeiculoPneuAplicado;
        this.posicaoVeiculoPneuAplicado = posicaoVeiculoPneuAplicado;
    }

    @NotNull
    public String getPlacaVeiculoPneuAplicado() {
        return placaVeiculoPneuAplicado;
    }

    @NotNull
    public Integer getPosicaoVeiculoPneuAplicado() {
        return posicaoVeiculoPneuAplicado;
    }
}
