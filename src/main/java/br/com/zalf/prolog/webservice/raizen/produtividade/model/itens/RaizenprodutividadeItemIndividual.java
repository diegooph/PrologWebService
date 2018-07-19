package br.com.zalf.prolog.webservice.raizen.produtividade.model.itens;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created on 19/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class RaizenprodutividadeItemIndividual {
    private Date data;
    private BigDecimal valor;
    private String placa;
    private String usina;
    private String fazenda;
    private double raio;
    private double toneladas;

    public Date getData() {
        return data;
    }

    public void setData(final Date data) {
        this.data = data;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(final BigDecimal valor) {
        this.valor = valor;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(final String placa) {
        this.placa = placa;
    }

    public String getUsina() {
        return usina;
    }

    public void setUsina(final String usina) {
        this.usina = usina;
    }

    public String getFazenda() {
        return fazenda;
    }

    public void setFazenda(final String fazenda) {
        this.fazenda = fazenda;
    }

    public double getRaio() {
        return raio;
    }

    public void setRaio(final double raio) {
        this.raio = raio;
    }

    public double getToneladas() {
        return toneladas;
    }

    public void setToneladas(final double toneladas) {
        this.toneladas = toneladas;
    }

    public double getValorAsDouble() {
        return valor.doubleValue();
    }
}