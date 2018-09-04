package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import java.time.LocalDateTime;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class IntervaloAjuste {

    private Long codIntervalo;
    private LocalDateTime dataHoraMarcacao;
    private Long codTipoIntervaloMarcado;
    private String nomeTipoIntervaloMarcado;
    private boolean temEdicao;
    private boolean isAtiva;

    public IntervaloAjuste() {
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

    public boolean isTemEdicao() {
        return temEdicao;
    }

    public void setTemEdicao(final boolean temEdicao) {
        this.temEdicao = temEdicao;
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
                ", temEdicao=" + temEdicao +
                ", isAtiva=" + isAtiva +
                '}';
    }
}
