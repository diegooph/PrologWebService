package br.com.zalf.prolog.webservice.gente.controleintervalo.justificativa_ajuste;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class JustificativaAjuste {
    private Long codJustificativaAjuste;
    private Long codEmpresa;
    private String nomeJustificativaAjuste;
    private boolean obrigatorioObservacao;
    private boolean isAtiva;

    public JustificativaAjuste() {
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
