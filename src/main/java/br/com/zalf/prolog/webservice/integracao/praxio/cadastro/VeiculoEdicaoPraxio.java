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
    private final Long novoKmAtualVeiculo;
    @NotNull
    private final Long novoCodModeloVeiculo;
    @NotNull
    private final Long novoCodTipoVeiculo;

    public VeiculoEdicaoPraxio(@NotNull final Long codUnidadeAlocado,
                               @NotNull final String placaVeiculo,
                               @NotNull final Long novoKmAtualVeiculo,
                               @NotNull final Long novoCodModeloVeiculo,
                               @NotNull final Long novoCodTipoVeiculo) {
        this.codUnidadeAlocado = codUnidadeAlocado;
        this.placaVeiculo = placaVeiculo;
        this.novoKmAtualVeiculo = novoKmAtualVeiculo;
        this.novoCodModeloVeiculo = novoCodModeloVeiculo;
        this.novoCodTipoVeiculo = novoCodTipoVeiculo;
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
    public Long getNovoKmAtualVeiculo() {
        return novoKmAtualVeiculo;
    }

    @NotNull
    public Long getCodModeloVeiculo() {
        return novoCodModeloVeiculo;
    }

    @NotNull
    public Long getNovoCodTipoVeiculo() {
        return novoCodTipoVeiculo;
    }
}
