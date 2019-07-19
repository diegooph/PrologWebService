package br.com.zalf.prolog.webservice.geral.dispositivo_movel.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 16/07/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class DispositivoMovel {
    @NotNull
    private final Long codDispositivo;
    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final String numeroImei;
    private final Long codMarca;
    private final String marca;
    private final String modelo;
    private final String descricao;

    public DispositivoMovel(@NotNull final Long codDispositivo,
                            @NotNull final Long codEmpresa,
                            @NotNull final String numeroImei,
                            final Long codMarca,
                            final String marca,
                            final String modelo,
                            final String descricao) {
        this.codDispositivo = codDispositivo;
        this.codEmpresa = codEmpresa;
        this.numeroImei = numeroImei;
        this.codMarca = codMarca;
        this.marca = marca;
        this.modelo = modelo;
        this.descricao = descricao;
    }

    @NotNull
    public Long getCodDispositivo() {
        return codDispositivo;
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

    public String getMarca() { return marca; }

    public String getModelo() {
        return modelo;
    }

    public String getDescricao() {
        return descricao;
    }
}
