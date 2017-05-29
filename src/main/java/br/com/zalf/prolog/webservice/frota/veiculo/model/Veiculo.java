package br.com.zalf.prolog.webservice.frota.veiculo.model;


import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;

import java.util.List;


/**
 * Created by jean on 04/04/16.
 */
public class Veiculo {

    private String placa;
    private Marca marca;
    private Modelo modelo;
    private TipoVeiculo tipo;

    @Deprecated
    private Eixos eixos;
    private long kmAtual;
    private boolean ativo;
    private List<Pneu> listPneus;
    private DiagramaVeiculo diagrama;

    public Veiculo() {

    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }

    public List<Pneu> getListPneus() {
        return listPneus;
    }

    public void setListPneus(List<Pneu> listPneus) {
        this.listPneus = listPneus;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public TipoVeiculo getTipo() {
        return tipo;
    }

    public void setTipo(TipoVeiculo tipo) {
        this.tipo = tipo;
    }

    public Eixos getEixos() {
        return eixos;
    }

    public void setEixos(Eixos eixos) {
        this.eixos = eixos;
    }

    public long getKmAtual() {
        return kmAtual;
    }

    public void setKmAtual(long km) {
        this.kmAtual = km;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public DiagramaVeiculo getDiagrama() {
        return diagrama;
    }

    public void setDiagrama(DiagramaVeiculo diagrama) {
        this.diagrama = diagrama;
    }

    public boolean temEstepe() {
        if (listPneus == null)
            return false;

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < listPneus.size(); i++) {
            if (listPneus.get(i).getPosicao() >= 900) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "Veiculo{" +
                "placa='" + placa + '\'' +
                ", marca=" + marca +
                ", modelo=" + modelo +
                ", tipo=" + tipo +
                ", kmAtual=" + kmAtual +
                ", ativo=" + ativo +
                ", listPneus=" + listPneus +
                ", diagramaVeiculo=" + diagrama +
                '}';
    }
}