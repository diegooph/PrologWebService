package br.com.zalf.prolog.webservice.integracao.praxio.cadastro;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 03/07/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class VeiculoEdicaoPraxio {
    @NotNull
    private final Long codUnidadeAlocado;
    @NotNull
    private final String placaVeiculo;
    @NotNull
    private final Long novoKmVeiculo;
    @NotNull
    private final Long novoCodModeloVeiculo;
    @NotNull
    private final Long novoCodTipoVeiculo;

    public VeiculoEdicaoPraxio(@NotNull final Long codUnidadeAlocado,
                               @NotNull final String placaVeiculo,
                               @NotNull final Long novoKmVeiculo,
                               @NotNull final Long novoCodModeloVeiculo,
                               @NotNull final Long novoCodTipoVeiculo) {
        this.codUnidadeAlocado = codUnidadeAlocado;
        this.placaVeiculo = placaVeiculo;
        this.novoKmVeiculo = novoKmVeiculo;
        this.novoCodModeloVeiculo = novoCodModeloVeiculo;
        this.novoCodTipoVeiculo = novoCodTipoVeiculo;
    }

    @NotNull
    public static VeiculoEdicaoPraxio getDummy() {
        return new VeiculoEdicaoPraxio(
                5L,
                "PRO0001",
                12345L,
                10L,
                20L);
    }

    @NotNull
    public Long getCodUnidadeAlocado() {
        return codUnidadeAlocado;
    }

    @NotNull
    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    @NotNull
    public Long getNovoKmVeiculo() {
        return novoKmVeiculo;
    }

    @NotNull
    public Long getNovoCodModeloVeiculo() {
        return novoCodModeloVeiculo;
    }

    @NotNull
    public Long getNovoCodTipoVeiculo() {
        return novoCodTipoVeiculo;
    }
}
