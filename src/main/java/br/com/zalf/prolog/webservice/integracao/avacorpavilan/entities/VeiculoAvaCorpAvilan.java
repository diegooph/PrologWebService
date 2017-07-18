package br.com.zalf.prolog.webservice.integracao.avacorpavilan.entities;

import br.com.zalf.prolog.webservice.integracao.BaseObjetoIntegracao;

/**
 * Created by luiz on 18/07/17.
 */
public final class VeiculoAvaCorpAvilan extends BaseObjetoIntegracao {
    private final String placa;
    private final String marcador;

    private VeiculoAvaCorpAvilan(String placa, String marcador) {
        this.placa = placa;
        this.marcador = marcador;
    }

    public String getPlaca() {
        return placa;
    }

    public String getMarcador() {
        return marcador;
    }


    public static final class Builder {
        private String placa;
        private String marcador;

        public Builder() {

        }

        public Builder withPlaca(String placa) {
            this.placa = placa;
            return this;
        }

        public Builder withMarcador(String marcador) {
            this.marcador = marcador;
            return this;
        }

        public VeiculoAvaCorpAvilan build() {
            return new VeiculoAvaCorpAvilan(placa, marcador);
        }
    }
}