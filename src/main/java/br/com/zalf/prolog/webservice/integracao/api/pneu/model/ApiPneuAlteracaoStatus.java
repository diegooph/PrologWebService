package br.com.zalf.prolog.webservice.integracao.api.pneu.model;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model.ApiStatusPneu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 21/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public abstract class ApiPneuAlteracaoStatus {
    @Exclude
    @NotNull
    private final ApiStatusPneu statusPneu;
    @NotNull
    private Long codigoSistemaIntegrado;
    @NotNull
    private String codigoCliente;
    @NotNull
    private Long codUnidadePneu;
    @NotNull
    private String cpfColaboradorAlteracaoStatus;
    @NotNull
    private OffsetDateTime dataHoraAlteracaoStatusUtc;
    private boolean trocouDeBanda;
    @Nullable
    private Long codNovoModeloBanda;
    @Nullable
    private BigDecimal valorNovaBandaPneu;

    public ApiPneuAlteracaoStatus(@NotNull final ApiStatusPneu statusPneu) {
        this.statusPneu = statusPneu;
    }

    public ApiPneuAlteracaoStatus(@NotNull final ApiStatusPneu statusPneu,
                                  @NotNull final Long codigoSistemaIntegrado,
                                  @NotNull final String codigoCliente,
                                  @NotNull final Long codUnidadePneu,
                                  @NotNull final String cpfColaboradorAlteracaoStatus,
                                  @NotNull final OffsetDateTime dataHoraAlteracaoStatusUtc,
                                  final boolean trocouDeBanda,
                                  @Nullable final Long codNovoModeloBanda,
                                  @Nullable final BigDecimal valorNovaBandaPneu) {
        this.codigoSistemaIntegrado = codigoSistemaIntegrado;
        this.codigoCliente = codigoCliente;
        this.codUnidadePneu = codUnidadePneu;
        this.cpfColaboradorAlteracaoStatus = cpfColaboradorAlteracaoStatus;
        this.dataHoraAlteracaoStatusUtc = dataHoraAlteracaoStatusUtc;
        this.statusPneu = statusPneu;
        this.trocouDeBanda = trocouDeBanda;
        this.codNovoModeloBanda = codNovoModeloBanda;
        this.valorNovaBandaPneu = valorNovaBandaPneu;
    }

    @NotNull
    public static RuntimeTypeAdapterFactory<ApiPneuAlteracaoStatus> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(ApiPneuAlteracaoStatus.class, "statusPneu")
                .registerSubtype(ApiPneuAlteracaoStatusAnalise.class, ApiStatusPneu.ANALISE.asString())
                .registerSubtype(ApiPneuAlteracaoStatusDescarte.class, ApiStatusPneu.DESCARTE.asString())
                .registerSubtype(ApiPneuAlteracaoStatusEstoque.class, ApiStatusPneu.ESTOQUE.asString())
                .registerSubtype(ApiPneuAlteracaoStatusVeiculo.class, ApiStatusPneu.EM_USO.asString());
    }

    @NotNull
    public static List<Long> getCodigoSistemaIntegradoPneus(
            @NotNull final List<ApiPneuAlteracaoStatus> pneusAtualizacaoStatus) {
        final List<Long> codigoSistemaIntegradoPneus = new ArrayList<>();
        pneusAtualizacaoStatus.forEach(
                apiPneuAlteracaoStatus ->
                        codigoSistemaIntegradoPneus.add(apiPneuAlteracaoStatus.getCodigoSistemaIntegrado()));
        return codigoSistemaIntegradoPneus;
    }

    @NotNull
    public ApiStatusPneu getStatusPneu() {
        return statusPneu;
    }

    @NotNull
    public Long getCodigoSistemaIntegrado() {
        return codigoSistemaIntegrado;
    }

    @NotNull
    public String getCodigoCliente() {
        return codigoCliente;
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
    public OffsetDateTime getDataHoraAlteracaoStatusUtc() {
        return dataHoraAlteracaoStatusUtc;
    }

    public boolean isTrocouDeBanda() {
        return trocouDeBanda;
    }

    @Nullable
    public Long getCodNovoModeloBanda() {
        return codNovoModeloBanda;
    }

    @Nullable
    public BigDecimal getValorNovaBandaPneu() {
        return valorNovaBandaPneu;
    }
}
