package br.com.zalf.prolog.webservice.geral.dispositivomovel.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    private final List<String> numerosImei;
    private final Long codMarca;
    private final String marca;
    private final String modelo;
    private final String descricao;

    public DispositivoMovel(@NotNull final Long codDispositivo,
                            @NotNull final Long codEmpresa,
                            @NotNull final List<String> numerosImei,
                            final Long codMarca,
                            final String marca,
                            final String modelo,
                            final String descricao) {
        this.codDispositivo = codDispositivo;
        this.codEmpresa = codEmpresa;
        this.numerosImei = numerosImei;
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
    public List<String> getNumerosImei() {
        return numerosImei;
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
