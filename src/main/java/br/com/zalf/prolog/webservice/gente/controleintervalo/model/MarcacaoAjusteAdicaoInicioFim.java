package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;

import java.time.LocalDateTime;
import java.util.Calendar;

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
        super(TipoMarcacaoAjuste.ADICAO_INICIO_FIM);
    }

    public static MarcacaoAjusteAdicaoInicioFim createDummy() {
        final MarcacaoAjusteAdicaoInicioFim adicaoInicioFim = new MarcacaoAjusteAdicaoInicioFim();
        adicaoInicioFim.setCodColaboradorMarcacao(2272L);
        adicaoInicioFim.setCodTipoIntervalo(10L);
        adicaoInicioFim.setDataHoraInicio(DateUtils.toLocalDateTime(Calendar.getInstance().getTime()));
        adicaoInicioFim.setDataHoraFim(DateUtils.toLocalDateTime(Calendar.getInstance().getTime()));
        adicaoInicioFim.setCodJustificativaAjuste(5L);
        adicaoInicioFim.setCodColaboradorAjuste(2272L);
        adicaoInicioFim.setNomeColaboradorAjuste("Zalf Sistemas");
        adicaoInicioFim.setObservacaoAjuste("Dummy Data");
        adicaoInicioFim.setDataHoraAjuste(DateUtils.toLocalDateTime(Calendar.getInstance().getTime()));
        return adicaoInicioFim;
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
