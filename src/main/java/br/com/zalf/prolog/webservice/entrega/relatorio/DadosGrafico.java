package br.com.zalf.prolog.webservice.entrega.relatorio;

import br.com.zalf.prolog.webservice.entrega.indicador.acumulado.IndicadorAcumulado;

import java.util.Date;

/**
 * Created by Zalf on 19/09/16.
 */
public class DadosGrafico {

    private Date data;
    private IndicadorAcumulado indicador;

    public DadosGrafico() {
    }

    public Date getData() {
        return data;
    }

    public DadosGrafico setData(Date data) {
        this.data = data;
        return this;
    }

    public IndicadorAcumulado getIndicador() {
        return indicador;
    }

    public DadosGrafico setIndicador(IndicadorAcumulado indicador) {
        this.indicador = indicador;
        return this;
    }

    @Override
    public String toString() {
        return "DadosGrafico{" +
                "data=" + data +
                ", indicador=" + indicador +
                '}';
    }
}
