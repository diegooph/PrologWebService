package br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.model.exibicao;

import br.com.zalf.prolog.webservice.gente.controlejornada.model.TipoMarcacao;
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
    private Long codTipoMarcacao;

    /**
     * O nome do {@link TipoMarcacao tipo de marcação} que foi realizado.
     */
    private String nomeTipoMarcacao;

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
        colaboradorAjuste.setCodTipoMarcacao(10L);
        colaboradorAjuste.setNomeTipoMarcacao("Refeição");
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

    public Long getCodTipoMarcacao() {
        return codTipoMarcacao;
    }

    public void setCodTipoMarcacao(final Long codTipoMarcacao) {
        this.codTipoMarcacao = codTipoMarcacao;
    }

    public String getNomeTipoMarcacao() {
        return nomeTipoMarcacao;
    }

    public void setNomeTipoMarcacao(final String nomeTipoMarcacao) {
        this.nomeTipoMarcacao = nomeTipoMarcacao;
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
                ", codTipoMarcacao=" + codTipoMarcacao +
                ", nomeTipoMarcacao='" + nomeTipoMarcacao + '\'' +
                ", temInconsistencia=" + temInconsistencia +
                ", jaFoiAjustada=" + jaFoiAjustada +
                '}';
    }
}
