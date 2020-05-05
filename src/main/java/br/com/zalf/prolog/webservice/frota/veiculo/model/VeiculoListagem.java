package br.com.zalf.prolog.webservice.frota.veiculo.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 04/05/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class VeiculoListagem {

    final String placa;
    final Long cod_unidade;
    final String km;
    final String status_ativo;
    final Long cod_tipo;
    final Long cod_modelo;
    final Long cod_eixos;
    final LocalDateTime data_hora_cadastro;
    final String cod_unidade_cadastro;
    final Long codigo;
    final Long cod_empresa;
    final Long cod_diagrama;
    final String numero_frota;
    final Long cod_regional_alocado;
    final String modelo;
    final String eixos;
    final String dianteiro;
    final String traseiro;
    final Long cod_eixos1;
    final String tipo;
    final String marca;
    final Long cod_marca;

    public VeiculoListagem(@NotNull final String placa,
                           @NotNull final Long cod_unidade,
                           @NotNull final String km,
                           @NotNull final String status_ativo,
                           @NotNull final Long cod_tipo,
                           @NotNull final Long cod_modelo,
                           @NotNull final Long cod_eixos,
                           @NotNull final LocalDateTime data_hora_cadastro,
                           @NotNull final String cod_unidade_cadastro,
                           @NotNull final Long codigo,
                           @NotNull final Long cod_empresa,
                           @NotNull final Long cod_diagrama,
                           @Nullable final String numero_frota,
                           @NotNull final Long cod_regional_alocado,
                           @NotNull final String modelo,
                           @NotNull final String eixos,
                           @NotNull final String dianteiro,
                           @NotNull final String traseiro,
                           @NotNull final Long cod_eixos1,
                           @NotNull final String tipo,
                           @NotNull final String marca,
                           @NotNull final Long cod_marca) {
        this.placa = placa;
        this.cod_unidade = cod_unidade;
        this.km = km;
        this.status_ativo = status_ativo;
        this.cod_tipo = cod_tipo;
        this.cod_modelo = cod_modelo;
        this.cod_eixos = cod_eixos;
        this.data_hora_cadastro = data_hora_cadastro;
        this.cod_unidade_cadastro = cod_unidade_cadastro;
        this.codigo = codigo;
        this.cod_empresa = cod_empresa;
        this.cod_diagrama = cod_diagrama;
        this.numero_frota = numero_frota;
        this.cod_regional_alocado = cod_regional_alocado;
        this.modelo = modelo;
        this.eixos = eixos;
        this.dianteiro = dianteiro;
        this.traseiro = traseiro;
        this.cod_eixos1 = cod_eixos1;
        this.tipo = tipo;
        this.marca = marca;
        this.cod_marca = cod_marca;
    }

    public String getPlaca() {
        return placa;
    }

    public Long getCod_unidade() {
        return cod_unidade;
    }

    public String getKm() {
        return km;
    }

    public String getStatus_ativo() {
        return status_ativo;
    }

    public Long getCod_tipo() {
        return cod_tipo;
    }

    public Long getCod_modelo() {
        return cod_modelo;
    }

    public Long getCod_eixos() {
        return cod_eixos;
    }

    public LocalDateTime getData_hora_cadastro() {
        return data_hora_cadastro;
    }

    public String getCod_unidade_cadastro() {
        return cod_unidade_cadastro;
    }

    public Long getCodigo() {
        return codigo;
    }

    public Long getCod_empresa() {
        return cod_empresa;
    }

    public Long getCod_diagrama() {
        return cod_diagrama;
    }

    public String getNumero_frota() {
        return numero_frota;
    }

    public Long getCod_regional_alocado() {
        return cod_regional_alocado;
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

    public Long getCod_eixos1() {
        return cod_eixos1;
    }

    public String getTipo() {
        return tipo;
    }

    public String getMarca() {
        return marca;
    }

    public Long getCod_marca() {
        return cod_marca;
    }
}
