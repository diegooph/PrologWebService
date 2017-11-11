package br.com.zalf.prolog.webservice.frota.pneu.afericao.model;

import java.util.List;

/**
 * Created by jean on 15/06/16.
 * Objeto utilizado para armazenar a lista de placas aferidas e pendentes de um determinado modelo de veículo
 */
public class PlacaModeloHolder {

    private String modelo;
    private List<PlacaStatus> placaStatus;
    private int qtdModeloSulcoOk;
    private int qtdModeloPressaoOk;
    private int qtdModeloSulcoPressaoOk;
    private int totalVeiculosModelo;

    public PlacaModeloHolder() {}

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public List<PlacaStatus> getPlacaStatus() {
        return placaStatus;
    }

    public void setPlacaStatus(List<PlacaStatus> placaStatus) {
        this.placaStatus = placaStatus;
    }

    public int getQtdModeloSulcoOk() {
        return qtdModeloSulcoOk;
    }

    public void setQtdModeloSulcoOk(int qtdModeloSulcoOk) {
        this.qtdModeloSulcoOk = qtdModeloSulcoOk;
    }

    public int getQtdModeloPressaoOk() {
        return qtdModeloPressaoOk;
    }

    public void setQtdModeloPressaoOk(int qtdModeloPressaoOk) {
        this.qtdModeloPressaoOk = qtdModeloPressaoOk;
    }

    public int getQtdModeloSulcoPressaoOk() {
        return qtdModeloSulcoPressaoOk;
    }

    public void setQtdModeloSulcoPressaoOk(int qtdModeloSulcoPressaoOk) {
        this.qtdModeloSulcoPressaoOk = qtdModeloSulcoPressaoOk;
    }

    public int getTotalVeiculosModelo() {
        return totalVeiculosModelo;
    }

    public void setTotalVeiculosModelo(int totalVieculosModelo) {
        this.totalVeiculosModelo = totalVieculosModelo;
    }

    @Override
    public String toString() {
        return "PlacaModeloHolder{" +
                "modelo='" + modelo + '\'' +
                ", placaStatus=" + placaStatus +
                ", qtdModeloSulcoOk=" + qtdModeloSulcoOk +
                ", qtdModeloPressaoOk=" + qtdModeloPressaoOk +
                ", qtdModeloSulcoPressaoOk=" + qtdModeloSulcoPressaoOk +
                ", totalVeiculosModelo=" + totalVeiculosModelo +
                '}';
    }

    public static class PlacaStatus {

        /**
         * se o valor de {@link #intervaloUltimaAfericaoSulco} ou {@link #intervaloUltimaAfericaoPressao}
         * for igual a essa constante, então essa placa nunca teve o sulco aferido.
         */
        public static final int INTERVALO_INVALIDO = -1;

        public String placa;

        /**
         * Número inteiro que representa a quantidade de dias desde a última aferição de Sulco.
         */
        public int intervaloUltimaAfericaoSulco;

        /**
         * Número inteiro que representa a quantidade de dias desde a última aferição da Pressão.
         */
        public int intervaloUltimaAfericaoPressao;

        /**
         * Indica quantos pneus estão vinculados a esse veículo.
         */
        public int quantidadePneus;

        public PlacaStatus() {}

        @Override
        public String toString() {
            return "PlacaStatus{" +
                    "placa='" + placa + '\'' +
                    ", intervaloUltimaAfericaoSulco=" + intervaloUltimaAfericaoSulco +
                    ", intervaloUltimaAfericaoPressao=" + intervaloUltimaAfericaoPressao +
                    '}';
        }
    }
}