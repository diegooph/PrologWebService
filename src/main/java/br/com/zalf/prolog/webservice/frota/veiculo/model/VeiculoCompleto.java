package br.com.zalf.prolog.webservice.frota.veiculo.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 04/05/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class VeiculoCompleto {

    @NotNull
    private final String placa;
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final String km;
    @NotNull
    private final String statusAtivo;
    @NotNull
    private final Long codTipo;
    @NotNull
    private final Long codModelo;
    @NotNull
    private final Long codEixos;
    @NotNull
    private final LocalDateTime dataHoraCadastro;
    @NotNull
    private final String codUnidadeCadastro;
    @NotNull
    private final Long codigo;
    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final Long codDiagrama;
    @Nullable
    private final String numeroFrota;
    @NotNull
    private final Long codRegionalAlocado;
    @NotNull
    private final String modelo;
    @NotNull
    private final String eixos;
    @NotNull
    private final String dianteiro;
    @NotNull
    private final String traseiro;
    @NotNull
    private final String tipo;
    @NotNull
    private final String marca;
    @NotNull
    private final Long codMarca;

    public VeiculoCompleto(@NotNull final String placa,
                           @NotNull final Long codUnidade,
                           @NotNull final String km,
                           @NotNull final String statusAtivo,
                           @NotNull final Long codTipo,
                           @NotNull final Long codModelo,
                           @NotNull final Long codEixos,
                           @NotNull final LocalDateTime dataHoraCadastro,
                           @NotNull final String codUnidadeCadastro,
                           @NotNull final Long codigo,
                           @NotNull final Long codEmpresa,
                           @NotNull final Long codDiagrama,
                           @Nullable final String numeroFrota,
                           @NotNull final Long codRegionalAlocado,
                           @NotNull final String modelo,
                           @NotNull final String eixos,
                           @NotNull final String dianteiro,
                           @NotNull final String traseiro,
                           @NotNull final String tipo,
                           @NotNull final String marca,
                           @NotNull final Long codMarca) {
        this.placa = placa;
        this.codUnidade = codUnidade;
        this.km = km;
        this.statusAtivo = statusAtivo;
        this.codTipo = codTipo;
        this.codModelo = codModelo;
        this.codEixos = codEixos;
        this.dataHoraCadastro = dataHoraCadastro;
        this.codUnidadeCadastro = codUnidadeCadastro;
        this.codigo = codigo;
        this.codEmpresa = codEmpresa;
        this.codDiagrama = codDiagrama;
        this.numeroFrota = numeroFrota;
        this.codRegionalAlocado = codRegionalAlocado;
        this.modelo = modelo;
        this.eixos = eixos;
        this.dianteiro = dianteiro;
        this.traseiro = traseiro;
        this.tipo = tipo;
        this.marca = marca;
        this.codMarca = codMarca;
    }

    public String getPlaca() {
        return placa;
    }

    public Long getCodUnidade() {
        return codUnidade;
    }

    public String getKm() {
        return km;
    }

    public String getStatusAtivo() {
        return statusAtivo;
    }

    public Long getCodTipo() {
        return codTipo;
    }

    public Long getCodModelo() {
        return codModelo;
    }

    public Long getCodEixos() {
        return codEixos;
    }

    public LocalDateTime getDataHoraCadastro() {
        return dataHoraCadastro;
    }

    public String getCodUnidadeCadastro() {
        return codUnidadeCadastro;
    }

    public Long getCodigo() {
        return codigo;
    }

    public Long getCodEmpresa() {
        return codEmpresa;
    }

    public Long getCodDiagrama() {
        return codDiagrama;
    }

    public String getNumeroFrota() {
        return numeroFrota;
    }

    public Long getCodRegionalAlocado() {
        return codRegionalAlocado;
    }

    public String getModelo() {
        return modelo;
    }

    public String getEixos() {
        return eixos;
    }

    public String getDianteiro() {
        return dianteiro;
    }

    public String getTraseiro() {
        return traseiro;
    }

    public String getTipo() {
        return tipo;
    }

    public String getMarca() {
        return marca;
    }

    public Long getCodMarca() {
        return codMarca;
    }
}
