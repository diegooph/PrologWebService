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
    private final Long kmAtualVeiculo;
    @NotNull
    private final Long codModeloVeiculo;
    @NotNull
    private final Long codTipoVeiculo;

    public VeiculoEdicaoPraxio(@NotNull final Long codUnidadeAlocado,
                               @NotNull final String placaVeiculo,
                               @NotNull final Long kmAtualVeiculo,
                               @NotNull final Long codModeloVeiculo,
                               @NotNull final Long codTipoVeiculo) {
        this.codUnidadeAlocado = codUnidadeAlocado;
        this.placaVeiculo = placaVeiculo;
        this.kmAtualVeiculo = kmAtualVeiculo;
        this.codModeloVeiculo = codModeloVeiculo;
        this.codTipoVeiculo = codTipoVeiculo;
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
    public Long getKmAtualVeiculo() {
        return kmAtualVeiculo;
    }

    @NotNull
    public Long getCodModeloVeiculo() {
        return codModeloVeiculo;
    }

    @NotNull
    public Long getCodTipoVeiculo() {
        return codTipoVeiculo;
    }
}
