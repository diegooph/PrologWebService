package br.com.zalf.prolog.webservice.geral.dispositivo_movel.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 6/27/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class DispositivoMovelInsercao {
    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final List<String> numerosImei;
    private final Long codMarca;
    private final String modelo;
    private final String descricao;

    public DispositivoMovelInsercao(
            @NotNull final Long codEmpresa,
            @NotNull final List<String> numerosImei,
            final Long codMarca,
            final String modelo,
            final String descricao) {
        this.codEmpresa = codEmpresa;
        this.codMarca = codMarca;
        this.numerosImei = numerosImei;
        this.modelo = modelo;
        this.descricao = descricao;
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

    public String getModelo() {
        return modelo;
    }

    public String getDescricao() {
        return descricao;
    }
}
