package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 24/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class VeiculoPlanilha {
    private Long codUnidade;
    private String placa;
    private String marca;
    private String modelo;
    private Long km;
    private String tipo;
    private String qtdEixos;

    public VeiculoPlanilha() {
    }

    public Long getCodUnidade() {
        return codUnidade;
    }

    public void setCodUnidade(@NotNull final Long codUnidade) {
        this.codUnidade = codUnidade;
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
}