package br.com.zalf.prolog.webservice.entrega.relatorio;

import br.com.zalf.prolog.webservice.entrega.indicador.item.IndicadorItem;

import java.util.Date;
import java.util.List;

/**
 * Created by Zalf on 15/09/16.
 */
public class MapaEstratificado {

    private int numeroMapa;
    private Date data;
    private String equipe;
    private String motorista;
    private String ajudante1;
    private String ajudante2;
    private String placa;
    private List<IndicadorItem> indicadores;

    public MapaEstratificado() {
    }

    public int getNumeroMapa() {
        return numeroMapa;
    }

    public MapaEstratificado setNumeroMapa(int numeroMapa) {
        this.numeroMapa = numeroMapa;
        return this;
    }

    public Date getData() {
        return data;
    }

    public MapaEstratificado setData(Date data) {
        this.data = data;
        return this;
    }

    public String getEquipe() {
        return equipe;
    }

    public MapaEstratificado setEquipe(String equipe) {
        this.equipe = equipe;
        return this;
    }

    public String getMotorista() {
        return motorista;
    }

    public MapaEstratificado setMotorista(String motorista) {
        this.motorista = motorista;
        return this;
    }

    public String getAjudante1() {
        return ajudante1;
    }

    public MapaEstratificado setAjudante1(String ajudante1) {
        this.ajudante1 = ajudante1;
        return this;
    }

    public String getAjudante2() {
        return ajudante2;
    }

    public MapaEstratificado setAjudante2(String ajudante2) {
        this.ajudante2 = ajudante2;
        return this;
    }

    public String getPlaca() {
        return placa;
    }

    public MapaEstratificado setPlaca(String placa) {
        this.placa = placa;
        return this;
    }

    public List<IndicadorItem> getIndicadores() {
        return indicadores;
    }

    public MapaEstratificado setIndicadores(List<IndicadorItem> indicadores) {
        this.indicadores = indicadores;
        return this;
    }

    @Override
    public String toString() {
        return "MapaEstratificado{" +
                "numeroMapa=" + numeroMapa +
                ", data=" + data +
                ", equipe='" + equipe + '\'' +
                ", motorista='" + motorista + '\'' +
                ", ajudante1='" + ajudante1 + '\'' +
                ", ajudante2='" + ajudante2 + '\'' +
                ", placa='" + placa + '\'' +
                ", indicadores=" + indicadores +
                '}';
    }
}
