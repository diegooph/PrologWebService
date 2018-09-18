package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao;

import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoMarcacao;
import org.jetbrains.annotations.NotNull;

/**
 * Representa uma marcação de início e fim de um colaborador, já vinculadas (agrupadas). Caso exista apenas início ou
 * apenas fim, também será representado por este objeto.
 *
 * Também contém qual o tipo de marcação que foi utilizado e se alguma das marcações (início ou fim) já foi ajustada
 * ou possui alguma inconsistência.
 *
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class MarcacaoColaboradorAjuste {
    /**
     * As marcações de início e fim. Podendo conter apenas início ou apenas fim.
     */
    private MarcacaoAgrupadaAjusteExibicao marcacoes;

    /**
     * O código do {@link TipoMarcacao tipo de marcação} que foi realizado.
     */
    private Long codTipoIntervaloMarcado;

    /**
     * O nome do {@link TipoMarcacao tipo de marcação} que foi realizado.
     */
    private String nomeTipoIntervaloMarcado;

    /**
     * Identifica se alguma das marcações tem alguma inconsistência.
     *
     * <code>true</code> se alguma das marcações (início ou fim), ou ambas, contiverem alguma inconsistência,
     * <code>false</code> caso contrário.
     */
    private boolean temInconsistencia;

    /**
     * Identifica se alguma das marcações já foi alguma vez ajustada.
     *
     * <code>true</code> se alguma das marcações (início ou fim), ou ambas, já foram ajustadas,
     * <code>false</code> caso contrário.
     */
    private boolean jaFoiAjustada;

    public MarcacaoColaboradorAjuste() {

    }

    @NotNull
    public static MarcacaoColaboradorAjuste createDummy() {
        final MarcacaoColaboradorAjuste colaboradorAjuste = new MarcacaoColaboradorAjuste();
        colaboradorAjuste.setCodTipoIntervaloMarcado(10L);
        colaboradorAjuste.setNomeTipoIntervaloMarcado("Refeição");
        colaboradorAjuste.setTemInconsistencia(true);
        colaboradorAjuste.setJaFoiAjustada(true);
        colaboradorAjuste.setMarcacoes(MarcacaoAgrupadaAjusteExibicao.createDummy());
        return colaboradorAjuste;
    }

    public MarcacaoAgrupadaAjusteExibicao getMarcacoes() {
        return marcacoes;
    }

    public void setMarcacoes(final MarcacaoAgrupadaAjusteExibicao marcacoes) {
        this.marcacoes = marcacoes;
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

    public boolean isJaFoiAjustada() {
        return jaFoiAjustada;
    }

    public void setJaFoiAjustada(final boolean jaFoiAjustada) {
        this.jaFoiAjustada = jaFoiAjustada;
    }

    @Override
    public String toString() {
        return "MarcacaoColaboradorAjuste{" +
                "intervaloAgrupadoAjuste=" + marcacoes +
                ", codTipoIntervaloMarcado=" + codTipoIntervaloMarcado +
                ", nomeTipoIntervaloMarcado='" + nomeTipoIntervaloMarcado + '\'' +
                ", temInconsistencia=" + temInconsistencia +
                ", jaFoiAjustada=" + jaFoiAjustada +
                '}';
    }
}
