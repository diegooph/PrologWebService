package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class IntervaloAjuste {

    private static final long TRINTA_MIN = 1800000;
    private Long codIntervalo;
    private LocalDateTime dataHoraMarcacao;
    private Long codTipoIntervaloMarcado;
    private String nomeTipoIntervaloMarcado;
    private boolean isAtiva;

    public IntervaloAjuste() {
    }

    @NotNull
    public static IntervaloAjuste createDummyInicio() {
        final IntervaloAjuste intervalo = new IntervaloAjuste();
        intervalo.setCodIntervalo(10101L);
        intervalo.setCodTipoIntervaloMarcado(10L);
        intervalo.setNomeTipoIntervaloMarcado("Refeição");
        intervalo.setAtiva(true);
        intervalo.setDataHoraMarcacao(DateUtils.toLocalDateTime(Calendar.getInstance().getTime()));
        return intervalo;
    }

    @NotNull
    public static IntervaloAjuste createDummyFim() {
        final IntervaloAjuste intervalo = new IntervaloAjuste();
        intervalo.setCodIntervalo(10101L);
        intervalo.setCodTipoIntervaloMarcado(10L);
        intervalo.setNomeTipoIntervaloMarcado("Refeição");
        intervalo.setAtiva(true);
        Calendar.getInstance().getTimeInMillis();
        intervalo.setDataHoraMarcacao(DateUtils.toLocalDateTime(new Date(System.currentTimeMillis() + TRINTA_MIN)));
        return intervalo;
    }

    public Long getCodIntervalo() {
        return codIntervalo;
    }

    public void setCodIntervalo(final Long codIntervalo) {
        this.codIntervalo = codIntervalo;
    }

    public LocalDateTime getDataHoraMarcacao() {
        return dataHoraMarcacao;
    }

    public void setDataHoraMarcacao(final LocalDateTime dataHoraMarcacao) {
        this.dataHoraMarcacao = dataHoraMarcacao;
    }

    public Long getCodTipoIntervaloMarcado() {
        return codTipoIntervaloMarcado;
    }

    public void setCodTipoIntervaloMarcado(final Long codTipoIntervaloMarcado) {
        this.codTipoIntervaloMarcado = codTipoIntervaloMarcado;
    }

    public String getNomeTipoIntervaloMarcado() {
        return nomeTipoIntervaloMarcado;
    }

    public void setNomeTipoIntervaloMarcado(final String nomeTipoIntervaloMarcado) {
        this.nomeTipoIntervaloMarcado = nomeTipoIntervaloMarcado;
    }

    public boolean isAtiva() {
        return isAtiva;
    }

    public void setAtiva(final boolean ativa) {
        isAtiva = ativa;
    }

    @Override
    public String toString() {
        return "IntervaloAjuste{" +
                "codIntervalo=" + codIntervalo +
                ", dataHoraMarcacao=" + dataHoraMarcacao +
                ", codTipoIntervaloMarcado=" + codTipoIntervaloMarcado +
                ", nomeTipoIntervaloMarcado='" + nomeTipoIntervaloMarcado + '\'' +
                ", isAtiva=" + isAtiva +
                '}';
    }
}
