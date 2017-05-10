package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import java.util.List;

/**
 * Created by jean on 15/06/16.
 * Objeto utilizado para armazenar a lista de placas aferidas e pendentes de um determinado modelo de veículo
 */
public class PlacaModeloHolder {

    private String modelo;
    private List<PlacaStatus> placaStatus;

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

    public PlacaModeloHolder() {
    }

    @Override
    public String toString() {
        return "PlacaModeloHolder{" +
                "modelo='" + modelo + '\'' +
                ", placaStatus=" + placaStatus +
                '}';
    }


    public static class PlacaStatus {

        /**
         * se o valor de {@link #intervaloUltimaAfericao} for igual a essa constante, então essa
         * placa nunca teve nenhuma aferição realizada
         */
        public static final int INTERVALO_INVALIDO = -1;

        public String placa;
        public int intervaloUltimaAfericao;

        /**
         * Indica quantos pneus estão vinculados a esse veículo
         */
        public int quantidadePneus;

        public PlacaStatus() {
        }

        @Override
        public String toString() {
            return "PlacaStatus{" +
                    "placa='" + placa + '\'' +
                    ", intervaloUltimaAfericao=" + intervaloUltimaAfericao +
                    '}';
        }
    }
}