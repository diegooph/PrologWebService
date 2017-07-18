package br.com.zalf.prolog.webservice.integracao.avacorpavilan.entities;

import br.com.zalf.prolog.webservice.integracao.BaseObjetoIntegracao;

/**
 * Created by luiz on 18/07/17.
 */
public final class PneuAvaCorpAvilan extends BaseObjetoIntegracao {
    private final String numeroFogo;
    private final String posicao;
    private final double sulco1;
    private final double sulco2;
    private final double sulco3;
    private final double sulco4;

    private PneuAvaCorpAvilan(String numeroFogo, String posicao, double sulco1, double sulco2, double sulco3, double sulco4) {
        this.numeroFogo = numeroFogo;
        this.posicao = posicao;
        this.sulco1 = sulco1;
        this.sulco2 = sulco2;
        this.sulco3 = sulco3;
        this.sulco4 = sulco4;
    }

    public String getNumeroFogo() {
        return numeroFogo;
    }

    public String getPosicao() {
        return posicao;
    }

    public double getSulco1() {
        return sulco1;
    }

    public double getSulco2() {
        return sulco2;
    }

    public double getSulco3() {
        return sulco3;
    }

    public double getSulco4() {
        return sulco4;
    }

    public static final class Builder {
        private String numeroFogo;
        private String posicao;
        private double sulco1;
        private double sulco2;
        private double sulco3;
        private double sulco4;

        public Builder() {

        }

        public Builder withNumeroFogo(String numeroFogo) {
            this.numeroFogo = numeroFogo;
            return this;
        }

        public Builder withPosicao(String posicao) {
            this.posicao = posicao;
            return this;
        }

        public Builder withSulco1(double sulco1) {
            this.sulco1 = sulco1;
            return this;
        }

        public Builder withSulco2(double sulco2) {
            this.sulco2 = sulco2;
            return this;
        }

        public Builder withSulco3(double sulco3) {
            this.sulco3 = sulco3;
            return this;
        }

        public Builder withSulco4(double sulco4) {
            this.sulco4 = sulco4;
            return this;
        }

        public PneuAvaCorpAvilan build() {
            return new PneuAvaCorpAvilan(numeroFogo, posicao, sulco1, sulco2, sulco3, sulco4);
        }
    }
}