package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class MarcacaoColaboradorAjuste {

    private IntervaloAgrupadoAjuste intervaloAgrupadoAjuste;
    private Long codTipoIntervaloMarcado;
    private String nomeTipoIntervaloMarcado;
    private boolean temInconsistencia;
    private boolean temEdicao;

    public MarcacaoColaboradorAjuste() {
    }

    @NotNull
    public static MarcacaoColaboradorAjuste createDummy() {
        final MarcacaoColaboradorAjuste colaboradorAjuste = new MarcacaoColaboradorAjuste();
        colaboradorAjuste.setCodTipoIntervaloMarcado(10L);
        colaboradorAjuste.setNomeTipoIntervaloMarcado("Refeição");
        colaboradorAjuste.setTemInconsistencia(true);
        colaboradorAjuste.setTemEdicao(true);
        colaboradorAjuste.setIntervaloAgrupadoAjuste(IntervaloAgrupadoAjuste.createDummy());
        return colaboradorAjuste;
    }

    public IntervaloAgrupadoAjuste getIntervaloAgrupadoAjuste() {
        return intervaloAgrupadoAjuste;
    }

    public void setIntervaloAgrupadoAjuste(final IntervaloAgrupadoAjuste intervaloAgrupadoAjuste) {
        this.intervaloAgrupadoAjuste = intervaloAgrupadoAjuste;
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

    public boolean isTemInconsistencia() {
        return temInconsistencia;
    }

    public void setTemInconsistencia(final boolean temInconsistencia) {
        this.temInconsistencia = temInconsistencia;
    }

    public boolean isTemEdicao() {
        return temEdicao;
    }

    public void setTemEdicao(final boolean temEdicao) {
        this.temEdicao = temEdicao;
    }

    @Override
    public String toString() {
        return "MarcacaoColaboradorAjuste{" +
                "intervaloAgrupadoAjuste=" + intervaloAgrupadoAjuste +
                ", codTipoIntervaloMarcado=" + codTipoIntervaloMarcado +
                ", nomeTipoIntervaloMarcado='" + nomeTipoIntervaloMarcado + '\'' +
                ", temInconsistencia=" + temInconsistencia +
                ", temEdicao=" + temEdicao +
                '}';
    }
}
