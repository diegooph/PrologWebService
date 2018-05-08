package br.com.zalf.prolog.webservice.frota.pneu.afericao.model;

import java.util.List;

/**
 * Created by jean on 15/06/16.
 * Objeto utilizado para armazenar a lista de placasAfericao aferidas e pendentes de um determinado nomeModelo de veículo
 */
public class ModeloPlacasAfericao {

    private String nomeModelo;
    private List<PlacaAfericao> placasAfericao;
    private int qtdModeloSulcoOk;
    private int qtdModeloPressaoOk;
    private int qtdModeloSulcoPressaoOk;
    private int totalVeiculosModelo;

    public ModeloPlacasAfericao() {}

    public String getNomeModelo() {
        return nomeModelo;
    }

    public void setNomeModelo(String nomeModelo) {
        this.nomeModelo = nomeModelo;
    }

    public List<PlacaAfericao> getPlacasAfericao() {
        return placasAfericao;
    }

    public void setPlacasAfericao(List<PlacaAfericao> placaAfericaos) {
        this.placasAfericao = placaAfericaos;
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
        return "ModeloPlacasAfericao{" +
                "nomeModelo='" + nomeModelo + '\'' +
                ", placasAfericao=" + placasAfericao +
                ", qtdModeloSulcoOk=" + qtdModeloSulcoOk +
                ", qtdModeloPressaoOk=" + qtdModeloPressaoOk +
                ", qtdModeloSulcoPressaoOk=" + qtdModeloSulcoPressaoOk +
                ", totalVeiculosModelo=" + totalVeiculosModelo +
                '}';
    }

    public static class PlacaAfericao {

        /**
         * se o valor de {@link #intervaloUltimaAfericaoSulco} ou {@link #intervaloUltimaAfericaoPressao}
         * for igual a essa constante, então essa placa nunca teve o sulco aferido.
         */
        public static final int INTERVALO_INVALIDO = -1;

        private String placa;

        /**
         * Número inteiro que representa a quantidade de dias desde a última aferição de Sulco.
         */
        private int intervaloUltimaAfericaoSulco;

        /**
         * Número inteiro que representa a quantidade de dias desde a última aferição da Pressão.
         */
        private int intervaloUltimaAfericaoPressao;

        /**
         * Indica quantos pneus estão vinculados a esse veículo.
         */
        private int quantidadePneus;

        /**
         * Indica se a {@link #placa} permite aferição do tipo {@link TipoAfericao#SULCO}.
         */
        private boolean podeAferirSulco;

        /**
         * Indica se a {@link #placa} permite aferição do tipo {@link TipoAfericao#PRESSAO}.
         */
        private boolean podeAferirPressao;

        /**
         * Indica se a {@link #placa} permite aferição do tipo {@link TipoAfericao#SULCO} e
         * do tipo {@link TipoAfericao#PRESSAO}.
         */
        private boolean podeAferirSUlcoPressao;

        public String getPlaca() {
            return placa;
        }

        public void setPlaca(String placa) {
            this.placa = placa;
        }

        public int getIntervaloUltimaAfericaoSulco() {
            return intervaloUltimaAfericaoSulco;
        }

        public void setIntervaloUltimaAfericaoSulco(int intervaloUltimaAfericaoSulco) {
            this.intervaloUltimaAfericaoSulco = intervaloUltimaAfericaoSulco;
        }

        public int getIntervaloUltimaAfericaoPressao() {
            return intervaloUltimaAfericaoPressao;
        }

        public void setIntervaloUltimaAfericaoPressao(int intervaloUltimaAfericaoPressao) {
            this.intervaloUltimaAfericaoPressao = intervaloUltimaAfericaoPressao;
        }

        public int getQuantidadePneus() {
            return quantidadePneus;
        }

        public void setQuantidadePneus(int quantidadePneus) {
            this.quantidadePneus = quantidadePneus;
        }

        public PlacaAfericao() {}

        @Override
        public String toString() {
            return "PlacaAfericao{" +
                    "placa='" + placa + '\'' +
                    ", intervaloUltimaAfericaoSulco=" + intervaloUltimaAfericaoSulco +
                    ", intervaloUltimaAfericaoPressao=" + intervaloUltimaAfericaoPressao +
                    '}';
        }
    }
}