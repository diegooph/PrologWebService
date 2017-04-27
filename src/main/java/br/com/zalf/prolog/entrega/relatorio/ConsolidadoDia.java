package br.com.zalf.prolog.entrega.relatorio;

import br.com.zalf.prolog.entrega.indicador.indicadores.acumulado.IndicadorAcumulado;

import java.util.Date;
import java.util.List;

/**
 * Created by jean on 13/09/16.
 * Usado na tela de relatórios.
 * Contém os dados consolidados de apenas um único dia.
 */
public class ConsolidadoDia {

    /*
    data ao qual os indicadores pertencem
     */
    public Date data;
    /*
    quantidade de mapas que saíram nesse dia
     */
    public int qtdMapas;
    /*
    Indicadores para preenchimento do card
     */
    public List<IndicadorAcumulado> indicadores;

    public ConsolidadoDia() {
    }

    public Date getData() {
        return data;
    }

    public ConsolidadoDia setData(Date data) {
        this.data = data;
        return this;
    }

    public int getQtdMapas() {
        return qtdMapas;
    }

    public ConsolidadoDia setQtdMapas(int qtdMapas) {
        this.qtdMapas = qtdMapas;
        return this;
    }

    public List<IndicadorAcumulado> getIndicadores() {
        return indicadores;
    }

    public ConsolidadoDia setIndicadores(List<IndicadorAcumulado> indicadores) {
        this.indicadores = indicadores;
        return this;
    }

    @Override
    public String toString() {
        return "ConsolidadoDia{" +
                "data=" + data +
                ", qtdMapas=" + qtdMapas +
                ", indicadores=" + indicadores +
                '}';
    }
}
