package br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model;

import br.com.zalf.prolog.webservice.commons.util.datetime.DateUtils;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Calendar;

/**
 * Classe utilizada quando é inserido um início e também um fim, de uma vez só, para um colaborador.
 *
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class MarcacaoAjusteAdicaoInicioFim extends MarcacaoAjuste {
    /**
     * Código do {@link Colaborador colaborador} para o qual as marcações de início e fim serão criadas.
     */
    private Long codColaboradorMarcacao;

    /**
     * Código do {@link TipoMarcacao tipo de marcação} para o qual as marcações de início e fim serão criadas.
     */
    private Long codTipoMarcacaoReferente;

    /**
     * Data e hora da marcação de início.
     */
    private LocalDateTime dataHoraInicio;

    /**
     * Data e hora da marcação de fim.
     */
    private LocalDateTime dataHoraFim;

    public MarcacaoAjusteAdicaoInicioFim() {
        super(TipoAcaoAjuste.ADICAO_INICIO_FIM);
    }

    @NotNull
    public static MarcacaoAjusteAdicaoInicioFim createDummy() {
        final MarcacaoAjusteAdicaoInicioFim adicaoInicioFim = new MarcacaoAjusteAdicaoInicioFim();
        adicaoInicioFim.setCodColaboradorMarcacao(2272L);
        adicaoInicioFim.setCodTipoMarcacaoReferente(10L);
        adicaoInicioFim.setDataHoraInicio(DateUtils.toLocalDateTime(Calendar.getInstance().getTime()));
        adicaoInicioFim.setDataHoraFim(DateUtils.toLocalDateTime(Calendar.getInstance().getTime()));
        adicaoInicioFim.setCodJustificativaAjuste(5L);
        adicaoInicioFim.setObservacaoAjuste("Dummy Data");
        adicaoInicioFim.setDataHoraAjuste(DateUtils.toLocalDateTime(Calendar.getInstance().getTime()));
        return adicaoInicioFim;
    }

    @Override
    public String toString() {
        return "MarcacaoAjusteAdicaoInicioFim{" +
                "codColaboradorMarcacao='" + codColaboradorMarcacao + '\'' +
                ", codTipoMarcacaoReferente=" + codTipoMarcacaoReferente +
                ", dataHoraInicio=" + dataHoraInicio +
                ", dataHoraFim=" + dataHoraFim +
                '}';
    }

    public Long getCodColaboradorMarcacao() {
        return codColaboradorMarcacao;
    }

    public void setCodColaboradorMarcacao(final Long codColaboradorMarcacao) {
        this.codColaboradorMarcacao = codColaboradorMarcacao;
    }

    public Long getCodTipoMarcacaoReferente() {
        return codTipoMarcacaoReferente;
    }

    public void setCodTipoMarcacaoReferente(final Long codTipoMarcacaoReferente) {
        this.codTipoMarcacaoReferente = codTipoMarcacaoReferente;
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
}
