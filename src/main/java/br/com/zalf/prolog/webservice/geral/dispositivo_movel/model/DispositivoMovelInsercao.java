package br.com.zalf.prolog.webservice.geral.dispositivo_movel.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 6/27/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class DispositivoMovelInsercao {
    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final String numeroImei;
    private final Long codMarca;
    private final String modelo;
    private final String descricao;

    public DispositivoMovelInsercao(@NotNull final Long codEmpresa,
                        @NotNull final String numeroImei,
                        final Long codMarca,
                        final String modelo,
                        final String descricao) {
        this.codEmpresa = codEmpresa;
        this.codMarca = codMarca;
        this.numeroImei = numeroImei;
        this.modelo = modelo;
        this.descricao = descricao;
    }

    @NotNull
    public Long getCodEmpresa() {
        return codEmpresa;
    }

    @NotNull
    public String getNumeroImei() {
        return numeroImei;
    }

    public Long getCodMarca() {
        return codMarca;
    }

    public String getModelo() {
        return modelo;
    }

    public String getDescricao() {
        return descricao;
    }
}
