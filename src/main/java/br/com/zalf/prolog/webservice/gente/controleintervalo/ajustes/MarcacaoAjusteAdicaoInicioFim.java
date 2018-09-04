package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes;

import br.com.zalf.prolog.webservice.gente.controleintervalo.model.MarcacaoAjuste;

import java.time.LocalDateTime;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class MarcacaoAjusteAdicaoInicioFim extends MarcacaoAjuste {

    private Long codColaboradorMarcacao;
    private Long codTipoIntervalo;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;

    public MarcacaoAjusteAdicaoInicioFim() {
    }

    public Long getCodColaboradorMarcacao() {
        return codColaboradorMarcacao;
    }

    public void setCodColaboradorMarcacao(final Long codColaboradorMarcacao) {
        this.codColaboradorMarcacao = codColaboradorMarcacao;
    }

    public Long getCodTipoIntervalo() {
        return codTipoIntervalo;
    }

    public void setCodTipoIntervalo(final Long codTipoIntervalo) {
        this.codTipoIntervalo = codTipoIntervalo;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(final LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(final LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    @Override
    public String toString() {
        return "MarcacaoAjusteAdicaoInicioFim{" +
                "codColaboradorMarcacao='" + codColaboradorMarcacao + '\'' +
                ", codTipoIntervalo=" + codTipoIntervalo +
                ", dataHoraInicio=" + dataHoraInicio +
                ", dataHoraFim=" + dataHoraFim +
                '}';
    }
}
