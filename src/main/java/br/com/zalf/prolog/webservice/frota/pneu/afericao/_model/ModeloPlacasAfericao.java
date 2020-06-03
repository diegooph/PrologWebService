package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;

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

    public ModeloPlacasAfericao() {
    }

    public String getNomeModelo() {
        return nomeModelo;
    }

    public void setNomeModelo(final String nomeModelo) {
        this.nomeModelo = nomeModelo;
    }

    public List<PlacaAfericao> getPlacasAfericao() {
        return placasAfericao;
    }

    public void setPlacasAfericao(final List<PlacaAfericao> placaAfericaos) {
        this.placasAfericao = placaAfericaos;
    }

    public int getQtdModeloSulcoOk() {
        return qtdModeloSulcoOk;
    }

    public void setQtdModeloSulcoOk(final int qtdModeloSulcoOk) {
        this.qtdModeloSulcoOk = qtdModeloSulcoOk;
    }

    public int getQtdModeloPressaoOk() {
        return qtdModeloPressaoOk;
    }

    public void setQtdModeloPressaoOk(final int qtdModeloPressaoOk) {
        this.qtdModeloPressaoOk = qtdModeloPressaoOk;
    }

    public int getQtdModeloSulcoPressaoOk() {
        return qtdModeloSulcoPressaoOk;
    }

    public void setQtdModeloSulcoPressaoOk(final int qtdModeloSulcoPressaoOk) {
        this.qtdModeloSulcoPressaoOk = qtdModeloSulcoPressaoOk;
    }

    public int getTotalVeiculosModelo() {
        return totalVeiculosModelo;
    }

    public void setTotalVeiculosModelo(final int totalVieculosModelo) {
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

        /**
         * Indentificar do veículo.
         */
        private String placa;

        /**
         * Código da unidade a qual a placa está vinculada.
         */
        private Long codUnidadePlaca;

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
         * Forma de coleta do sulco
         */
        private FormaColetaDadosAfericaoEnum formaColetaDadosSulco;

        /**
         * @deprecated Agora deve ser utilizado o Enum formaColetaDadosSulco.
         * Este atributo foi mantido para permitir o funcionamento de apps antigos.
         * Indica se a {@link #placa} permite aferição do tipo {@link TipoMedicaoColetadaAfericao#SULCO}.
         */
        @Deprecated
        private boolean podeAferirSulco;

        /**
         * Forma de coleta do pressão
         */
        private FormaColetaDadosAfericaoEnum formaColetaDadosPressao;

        /**
         * @deprecated Agora deve ser utilizado o Enum formaColetaDadosPressao.
         * Este atributo foi mantido para permitir o funcionamento de apps antigos.
         * Indica se a {@link #placa} permite aferição do tipo {@link TipoMedicaoColetadaAfericao#PRESSAO}.
         */
        @Deprecated
        private boolean podeAferirPressao;

        /**
         * Forma de coleta do sulco e pressão
         */
        private FormaColetaDadosAfericaoEnum formaColetaDadosSulcoPressao;

        /**
         * @deprecated Agora deve ser utilizado o Enum formaColetaDadosSulcoPressao.
         * Este atributo foi mantido para permitir o funcionamento de apps antigos.
         * Indica se a {@link #placa} permite aferição do tipo {@link TipoMedicaoColetadaAfericao#SULCO} e
         * do tipo {@link TipoMedicaoColetadaAfericao#PRESSAO}.
         */
        @Deprecated
        private boolean podeAferirSulcoPressao;

        /**
         * Indica se a {@link #placa} permite aferição de estepes.
         */
        private boolean podeAferirEstepe;

        /**
         * Quantidade de dias que a aferição de sulco deve ser repetida.
         */
        private int metaAfericaoSulco;

        /**
         * Quantidade de dias que a aferição de pressão deve ser repetida.
         */
        private int metaAfericaoPressao;

        public PlacaAfericao() {
        }

        public FormaColetaDadosAfericaoEnum getFormaColetaDadosSulco() {
            return formaColetaDadosSulco;
        }

        public void setFormaColetaDadosSulco(final FormaColetaDadosAfericaoEnum formaColetaDadosSulco) {
            this.formaColetaDadosSulco = formaColetaDadosSulco;
            this.podeAferirSulco = formaColetaDadosSulco != FormaColetaDadosAfericaoEnum.BLOQUEADO;
        }

        public FormaColetaDadosAfericaoEnum getFormaColetaDadosPressao() {
            return formaColetaDadosPressao;
        }

        public void setFormaColetaDadosPressao(final FormaColetaDadosAfericaoEnum formaColetaDadosPressao) {
            this.formaColetaDadosPressao = formaColetaDadosPressao;
            this.podeAferirPressao = formaColetaDadosPressao != FormaColetaDadosAfericaoEnum.BLOQUEADO;
        }

        public FormaColetaDadosAfericaoEnum getFormaColetaDadosSulcoPressao() {
            return formaColetaDadosSulcoPressao;
        }

        public void setFormaColetaDadosSulcoPressao(final FormaColetaDadosAfericaoEnum formaColetaDadosSulcoPressao) {
            this.formaColetaDadosSulcoPressao = formaColetaDadosSulcoPressao;
            this.podeAferirSulcoPressao = formaColetaDadosSulcoPressao != FormaColetaDadosAfericaoEnum.BLOQUEADO;
        }

        public String getPlaca() {
            return placa;
        }

        public void setPlaca(final String placa) {
            this.placa = placa;
        }

        public Long getCodUnidadePlaca() {
            return codUnidadePlaca;
        }

        public void setCodUnidadePlaca(final Long codUnidadePlaca) {
            this.codUnidadePlaca = codUnidadePlaca;
        }

        public int getIntervaloUltimaAfericaoSulco() {
            return intervaloUltimaAfericaoSulco;
        }

        public void setIntervaloUltimaAfericaoSulco(final int intervaloUltimaAfericaoSulco) {
            this.intervaloUltimaAfericaoSulco = intervaloUltimaAfericaoSulco;
        }

        public int getIntervaloUltimaAfericaoPressao() {
            return intervaloUltimaAfericaoPressao;
        }

        public void setIntervaloUltimaAfericaoPressao(final int intervaloUltimaAfericaoPressao) {
            this.intervaloUltimaAfericaoPressao = intervaloUltimaAfericaoPressao;
        }

        public int getQuantidadePneus() {
            return quantidadePneus;
        }

        public void setQuantidadePneus(final int quantidadePneus) {
            this.quantidadePneus = quantidadePneus;
        }

        public boolean isPodeAferirSulco() {
            return podeAferirSulco;
        }

        public boolean isPodeAferirPressao() {
            return podeAferirPressao;
        }

        public boolean isPodeAferirSulcoPressao() {
            return podeAferirSulcoPressao;
        }

        public boolean isPodeAferirEstepe() {
            return podeAferirEstepe;
        }

        public void setPodeAferirEstepe(final boolean podeAferirEstepe) {
            this.podeAferirEstepe = podeAferirEstepe;
        }

        public int getMetaAfericaoSulco() {
            return metaAfericaoSulco;
        }

        public void setMetaAfericaoSulco(final int metaAfericaoSulco) {
            this.metaAfericaoSulco = metaAfericaoSulco;
        }

        public int getMetaAfericaoPressao() {
            return metaAfericaoPressao;
        }

        public void setMetaAfericaoPressao(final int metaAfericaoPressao) {
            this.metaAfericaoPressao = metaAfericaoPressao;
        }

        public boolean isAfericaoSulcoNoPrazo(final int metaSulco) {
            return !isAfericaoSulcoVencidaOuNuncaAferida(metaSulco);
        }

        public boolean isAfericaoPressaoNoPrazo(final int metaPressao) {
            return !isAfericaoPressaoVencidaOuNuncaAferida(metaPressao);
        }

        private boolean isAfericaoPressaoVencidaOuNuncaAferida(final int metaPressao) {
            return intervaloUltimaAfericaoPressao > metaPressao
                    || intervaloUltimaAfericaoPressao == PlacaAfericao.INTERVALO_INVALIDO;
        }

        private boolean isAfericaoSulcoVencidaOuNuncaAferida(final int metaSulco) {
            return intervaloUltimaAfericaoSulco > metaSulco
                    || intervaloUltimaAfericaoSulco == PlacaAfericao.INTERVALO_INVALIDO;
        }

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