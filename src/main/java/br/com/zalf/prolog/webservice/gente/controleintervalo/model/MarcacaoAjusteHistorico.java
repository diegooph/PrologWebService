package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import java.time.LocalDateTime;

/**
 * Created on 05/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class MarcacaoAjusteHistorico {
    private String nomeColaboradorAjuste;
    private String nomeJustificativaAjuste;
    private String observacaoAjuste;
    private LocalDateTime dataHoraAjuste;
    private String descricaoAcaoRealizada;

    public MarcacaoAjusteHistorico() {
    }

    public String getNomeColaboradorAjuste() {
        return nomeColaboradorAjuste;
    }

    public void setNomeColaboradorAjuste(final String nomeColaboradorAjuste) {
        this.nomeColaboradorAjuste = nomeColaboradorAjuste;
    }

    public String getNomeJustificativaAjuste() {
        return nomeJustificativaAjuste;
    }

    public void setNomeJustificativaAjuste(final String nomeJustificativaAjuste) {
        this.nomeJustificativaAjuste = nomeJustificativaAjuste;
    }

    public String getObservacaoAjuste() {
        return observacaoAjuste;
    }

    public void setObservacaoAjuste(final String observacaoAjuste) {
        this.observacaoAjuste = observacaoAjuste;
    }

    public LocalDateTime getDataHoraAjuste() {
        return dataHoraAjuste;
    }

    public void setDataHoraAjuste(final LocalDateTime dataHoraAjuste) {
        this.dataHoraAjuste = dataHoraAjuste;
    }

    public String getDescricaoAcaoRealizada() {
        return descricaoAcaoRealizada;
    }

    public void setDescricaoAcaoRealizada(final String descricaoAcaoRealizada) {
        this.descricaoAcaoRealizada = descricaoAcaoRealizada;
    }

    @Override
    public String toString() {
        return "MarcacaoAjusteHistorico{" +
                "nomeColaboradorAjuste='" + nomeColaboradorAjuste + '\'' +
                ", nomeJustificativaAjuste='" + nomeJustificativaAjuste + '\'' +
                ", observacaoAjuste='" + observacaoAjuste + '\'' +
                ", dataHoraAjuste=" + dataHoraAjuste +
                ", descricaoAcaoRealizada='" + descricaoAcaoRealizada + '\'' +
                '}';
    }
}
