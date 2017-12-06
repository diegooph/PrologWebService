package br.com.zalf.prolog.webservice.frota.pneu.servico.model;

import java.util.List;

/**
 * Created by jean on 09/04/16.
 */
public class PlacaServicoHolder {
    private List<PlacaServico> listPlacas;
    private int qtCalibragemTotal;
    private int qtMovimentacaoTotal;
    private int qtInspecaoTotal;

    public PlacaServicoHolder() {

    }

    public List<PlacaServico> getListPlacas() {
        return listPlacas;
    }

    public int getQtInspecaoTotal() {
        return qtInspecaoTotal;
    }

    public void setQtInspecaoTotal(int qtInspecaoTotal) {
        this.qtInspecaoTotal = qtInspecaoTotal;
    }

    public void setListPlacas(List<PlacaServico> listPlacas) {
        this.listPlacas = listPlacas;
    }

    public int getQtCalibragemTotal() {
        return qtCalibragemTotal;
    }

    public void setQtCalibragemTotal(int qtCalibragemTotal) {
        this.qtCalibragemTotal = qtCalibragemTotal;
    }

    public int getQtMovimentacaoTotal() {
        return qtMovimentacaoTotal;
    }

    public void setQtMovimentacaoTotal(int qtMovimentacaoTotal) {
        this.qtMovimentacaoTotal = qtMovimentacaoTotal;
    }

    @Override
    public String toString() {
        return "PlacaServicoHolder{" +
                "listPlacas=" + listPlacas +
                ", qtCalibragemTotal=" + qtCalibragemTotal +
                ", qtMovimentacaoTotal=" + qtMovimentacaoTotal +
                ", qtInspecaoTotal=" + qtInspecaoTotal +
                '}';
    }

    public static class PlacaServico {

        public String placa;
        public int qtCalibragem;
        public int qtMovimentacao;
        public int qtInspecao;

        public PlacaServico() {

        }

        @Override
        public String toString() {
            return "PlacaServico{" +
                    "placa='" + placa + '\'' +
                    ", qtCalibragem=" + qtCalibragem +
                    ", qtMovimentacao=" + qtMovimentacao +
                    ", qtInspecaoTotal=" + qtInspecao +
                    '}';
        }
    }

}