package br.com.zalf.prolog.webservice.frota.veiculo.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-10-04
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class VeiculoCadastro {
    @NotNull
    private final Long codEmpresaAlocado;
    @NotNull
    private final Long codUnidadeAlocado;
    @NotNull
    private final String placaVeiculo;
    @NotNull
    private final Long codMarcaVeiculo;
    @NotNull
    private final Long codModeloVeiculo;
    @NotNull
    private final Long codTipoVeiculo;
    private final long kmAtualVeiculo;

    public VeiculoCadastro(@NotNull final Long codEmpresaAlocado,
                           @NotNull final Long codUnidadeAlocado,
                           @NotNull final String placaVeiculo,
                           @NotNull final Long codMarcaVeiculo,
                           @NotNull final Long codModeloVeiculo,
                           @NotNull final Long codTipoVeiculo,
                           final long kmAtualVeiculo) {
        this.codEmpresaAlocado = codEmpresaAlocado;
        this.codUnidadeAlocado = codUnidadeAlocado;
        this.placaVeiculo = placaVeiculo;
        this.codMarcaVeiculo = codMarcaVeiculo;
        this.codModeloVeiculo = codModeloVeiculo;
        this.codTipoVeiculo = codTipoVeiculo;
        this.kmAtualVeiculo = kmAtualVeiculo;
    }

    @NotNull
    public Long getCodEmpresaAlocado() {
        return codEmpresaAlocado;
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
    public Long getCodMarcaVeiculo() {
        return codMarcaVeiculo;
    }

    @NotNull
    public Long getCodModeloVeiculo() {
        return codModeloVeiculo;
    }

    @NotNull
    public Long getCodTipoVeiculo() {
        return codTipoVeiculo;
    }

    public long getKmAtualVeiculo() {
        return kmAtualVeiculo;
    }
}