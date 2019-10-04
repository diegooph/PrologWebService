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
    private final String placa;
    @NotNull
    private final Long codMarca;
    @NotNull
    private final Long codModelo;
    @NotNull
    private final Long codTipoVeiculo;
    private final long kmAtual;

    public VeiculoCadastro(@NotNull final Long codEmpresaAlocado,
                           @NotNull final Long codUnidadeAlocado,
                           @NotNull final String placa,
                           @NotNull final Long codMarca,
                           @NotNull final Long codModelo,
                           @NotNull final Long codTipoVeiculo,
                           final long kmAtual) {
        this.codEmpresaAlocado = codEmpresaAlocado;
        this.codUnidadeAlocado = codUnidadeAlocado;
        this.placa = placa;
        this.codMarca = codMarca;
        this.codModelo = codModelo;
        this.codTipoVeiculo = codTipoVeiculo;
        this.kmAtual = kmAtual;
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
    public String getPlaca() {
        return placa;
    }

    @NotNull
    public Long getCodMarca() {
        return codMarca;
    }

    @NotNull
    public Long getCodModelo() {
        return codModelo;
    }

    @NotNull
    public Long getCodTipoVeiculo() {
        return codTipoVeiculo;
    }

    public long getKmAtual() {
        return kmAtual;
    }
}