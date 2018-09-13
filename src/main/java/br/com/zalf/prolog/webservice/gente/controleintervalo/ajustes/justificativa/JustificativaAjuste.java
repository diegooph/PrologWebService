package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.justificativa;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class JustificativaAjuste {
    private Long codJustificativaAjuste;
    private Long codEmpresa;
    private String nomeJustificativaAjuste;
    private boolean obrigatorioObservacao;
    private boolean isAtiva;

    public JustificativaAjuste() {
    }

    @NotNull
    public static JustificativaAjuste createDummy() {
        final JustificativaAjuste justificativa = new JustificativaAjuste();
        justificativa.setCodJustificativaAjuste(10L);
        justificativa.setCodEmpresa(3L);
        justificativa.setNomeJustificativaAjuste("Esquecimento");
        justificativa.setObrigatorioObservacao(true);
        justificativa.setAtiva(true);
        return justificativa;
    }

    public Long getCodJustificativaAjuste() {
        return codJustificativaAjuste;
    }

    public void setCodJustificativaAjuste(final Long codJustificativaAjuste) {
        this.codJustificativaAjuste = codJustificativaAjuste;
    }

    public Long getCodEmpresa() {
        return codEmpresa;
    }

    public void setCodEmpresa(final Long codEmpresa) {
        this.codEmpresa = codEmpresa;
    }

    public String getNomeJustificativaAjuste() {
        return nomeJustificativaAjuste;
    }

    public void setNomeJustificativaAjuste(final String nomeJustificativaAjuste) {
        this.nomeJustificativaAjuste = nomeJustificativaAjuste;
    }

    public boolean isObrigatorioObservacao() {
        return obrigatorioObservacao;
    }

    public void setObrigatorioObservacao(final boolean obrigatorioObservacao) {
        this.obrigatorioObservacao = obrigatorioObservacao;
    }

    public boolean isAtiva() {
        return isAtiva;
    }

    public void setAtiva(final boolean ativa) {
        isAtiva = ativa;
    }

    @Override
    public String toString() {
        return "JustificativaAjuste{" +
                "codJustificativaAjuste=" + codJustificativaAjuste +
                ", codEmpresa=" + codEmpresa +
                ", nomeJustificativaAjuste='" + nomeJustificativaAjuste + '\'' +
                ", obrigatorioObservacao=" + obrigatorioObservacao +
                ", isAtiva=" + isAtiva +
                '}';
    }
}
