package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import java.time.LocalDateTime;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public abstract class MarcacaoAjuste {

    private Long codJustificativaAjuste;
    private Long codColaboradorAjuste;
    private String nomeColaboradorAjuste;
    private String observacaoAjuste;
    private LocalDateTime dataHoraAjuste;

    public Long getCodJustificativaAjuste() {
        return codJustificativaAjuste;
    }

    public void setCodJustificativaAjuste(final Long codJustificativaAjuste) {
        this.codJustificativaAjuste = codJustificativaAjuste;
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

    public Long getCodColaboradorAjuste() {
        return codColaboradorAjuste;
    }

    public void setCodColaboradorAjuste(final Long codColaboradorAjuste) {
        this.codColaboradorAjuste = codColaboradorAjuste;
    }

    public String getNomeColaboradorAjuste() {
        return nomeColaboradorAjuste;
    }

    public void setNomeColaboradorAjuste(final String nomeColaboradorAjuste) {
        this.nomeColaboradorAjuste = nomeColaboradorAjuste;
    }

    @Override
    public String toString() {
        return "MarcacaoAjuste{" +
                "codJustificativaAjuste=" + codJustificativaAjuste +
                ", observacaoAjuste='" + observacaoAjuste + '\'' +
                ", dataHoraAjuste=" + dataHoraAjuste +
                ", codColaboradorAjuste=" + codColaboradorAjuste +
                ", nomeColaboradorAjuste='" + nomeColaboradorAjuste + '\'' +
                '}';
    }
}
