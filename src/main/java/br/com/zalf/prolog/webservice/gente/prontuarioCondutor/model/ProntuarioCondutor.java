package br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ocorrencia.*;
import com.google.common.base.Preconditions;

/**
 * Created by Zart on 03/07/2017.
 */
public final class ProntuarioCondutor {

    /**
     * Faixa de valores no qual a {@link #pontuacaoTotalPonderada} pode variar;
     * Por exemplo, uma pontuação de 10, estaria na faixa {@link Faixa#AMARELO}. E uma pontuação
     * de 29.9 estaria na faixa {@link Faixa#LARANJA}.
     */
    public enum Faixa {
        VERDE(0, 10, "#00FF00"),
        AMARELO(10, 20, "#00FF00"),
        LARANJA(20, 30, "#00FF00"),
        VERMELHO(30, 40, "#00FF00"),
        PRETO(40, Double.MAX_VALUE, "#00FF00");

        private double inicioFaixa;
        private double fimFaixa;
        private String corHex;

        /**
         * @param de início da faixa, exclusivo
         * @param ate término da faixa, inclusivo
         * @param corHex a cor dessa faixa
         */
        Faixa(double de, double ate, String corHex) {
            this.inicioFaixa = de;
            this.fimFaixa = ate;
            this.corHex = corHex;
        }

        public double getInicioFaixa() {
            return inicioFaixa;
        }

        public double getFimFaixa() {
            return fimFaixa;
        }

        public String getCorHex() {
            return corHex;
        }

        public static Faixa fromPontuacao(final double pontuacao) {
            Preconditions.checkArgument(pontuacao >= 0, "pontuacao não pode ser menor do que 0!");

            if (pontuacao <= 10) {
                return VERDE;
            } else if (pontuacao > 10 && pontuacao <= 20) {
                return AMARELO;
            } else if (pontuacao > 20 && pontuacao <= 30) {
                return LARANJA;
            } else if (pontuacao > 30 && pontuacao <= 40) {
                return VERMELHO;
            }
            return PRETO;
        }
    }

    private double pontuacaoTotalPonderada;
    private Faixa faixa;
    private Colaborador colaborador;
    private AcidentesTrabalho acidentesTrabalho;
    private AcidentesTransito acidentesTransito;
    private GerenciamentoFadigas gerenciamentoFadigas;
    private Cnh cnh;
    private Documento documento;
    private Indisciplina indisciplina;
    private Multas multas;
    private Sac sac;
    private Sav sav;
    private Situacao situacao;
    private Telemetria telemetria;

    public ProntuarioCondutor() {
    }


    public void setFaixa(final Faixa faixa) {
        this.faixa = faixa;
    }

    public GerenciamentoFadigas getGerenciamentoFadigas() {
        return gerenciamentoFadigas;
    }

    public void setGerenciamentoFadigas(final GerenciamentoFadigas gerenciamentoFadigas) {
        this.gerenciamentoFadigas = gerenciamentoFadigas;
    }

    public Faixa getFaixa() {
        return faixa;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public AcidentesTrabalho getAcidentesTrabalho() {
        return acidentesTrabalho;
    }

    public void setAcidentesTrabalho(AcidentesTrabalho acidentesTrabalho) {
        this.acidentesTrabalho = acidentesTrabalho;
    }

    public AcidentesTransito getAcidentesTransito() {
        return acidentesTransito;
    }

    public void setAcidentesTransito(AcidentesTransito acidentesTransito) {
        this.acidentesTransito = acidentesTransito;
    }

    public Cnh getCnh() {
        return cnh;
    }

    public void setCnh(Cnh cnh) {
        this.cnh = cnh;
    }

    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    public Indisciplina getIndisciplina() {
        return indisciplina;
    }

    public void setIndisciplina(Indisciplina indisciplina) {
        this.indisciplina = indisciplina;
    }

    public Multas getMultas() {
        return multas;
    }

    public void setMultas(Multas multas) {
        this.multas = multas;
    }

    public Sac getSac() {
        return sac;
    }

    public void setSac(Sac sac) {
        this.sac = sac;
    }

    public Sav getSav() {
        return sav;
    }

    public void setSav(Sav sav) {
        this.sav = sav;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public Telemetria getTelemetria() {
        return telemetria;
    }

    public void setTelemetria(Telemetria telemetria) {
        this.telemetria = telemetria;
    }

    public double getPontuacaoTotalPonderada() {
        return pontuacaoTotalPonderada;
    }

    public void setPontuacaoTotalPonderada(double pontuacaoTotal) {
        this.pontuacaoTotalPonderada = pontuacaoTotal;
        this.faixa = Faixa.fromPontuacao(pontuacaoTotal);
    }
}
