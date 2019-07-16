package br.com.zalf.prolog.webservice.geral.imei.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 16/07/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class Imei {
    @NotNull
    private final Long codImei;
    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final String numeroImei;
    private final Long codMarca;
    private final String modelo;
    private final String descricao;

    public Imei(@NotNull final Long codImei,
                @NotNull final Long codEmpresa,
                @NotNull final String numeroImei,
                final Long codMarca,
                final String modelo,
                final String descricao) {
        this.codImei = codImei;
        this.codEmpresa = codEmpresa;
        this.codMarca = codMarca;
        this.numeroImei = numeroImei;
        this.modelo = modelo;
        this.descricao = descricao;
    }

    @NotNull
    public Long getCodImei() {
        return codImei;
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
