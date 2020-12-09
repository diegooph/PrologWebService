package br.com.zalf.prolog.webservice.interno.implantacao.conferencia.frota.veiculo._model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 24/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class VeiculoPlanilha {
    private String placa;
    private String marca;
    private String modelo;
    private Long km;
    private String tipo;
    private String qtdEixos;
    private String identificadorFrota;
    private String possuiHubodometro;

    public VeiculoPlanilha() {
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(@NotNull final String placa) {
        this.placa = placa;
    }

    public Long getKm() {
        return km;
    }

    public void setKm(@NotNull final Long km) {
        this.km = km;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(@NotNull final String tipo) {
        this.tipo = tipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(@NotNull final String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(@NotNull final String modelo) {
        this.modelo = modelo;
    }

    public String getQtdEixos() {
        return qtdEixos;
    }

    public void setQtdEixos(@NotNull final String qtdEixos) {
        this.qtdEixos = qtdEixos;
    }

    public String getIdentificadorFrota() {
        return identificadorFrota;
    }

    public void setIdentificadorFrota(final String identificadorFrota) {
        this.identificadorFrota = identificadorFrota;
    }

    public String getPossuiHubodometro() {
        return possuiHubodometro;
    }

    public void setPossuiHubodometro(final String motorizado) {
        this.possuiHubodometro = motorizado;
    }
}