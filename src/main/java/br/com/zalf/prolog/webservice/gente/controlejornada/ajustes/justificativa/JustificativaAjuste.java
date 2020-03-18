package br.com.zalf.prolog.webservice.gente.controlejornada.ajustes.justificativa;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Empresa;
import org.jetbrains.annotations.NotNull;

/**
 * Representa uma opção de justificativa disponível ao se realizar o ajuste de uma marcação.
 *
 * O usuário é obrigado a selecionar uma justificativa sempre que um ajuste é realizado.
 *
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class JustificativaAjuste {
    /**
     * Código único dessa justificativa.
     */
    private Long codigo;

    /**
     * Código da {@link Empresa empresa} do qual essa justificativa pertence.
     */
    private Long codEmpresa;

    /**
     * Nome dessa justificativa.
     */
    private String nomeJustificativaAjuste;

    /**
     * Indica se para esta justificativa, é obrigatório fornecer uma observação.
     */
    private boolean obrigatorioObservacao;

    /**
     * Indica se esta justificativa está ativa.
     * <code>true</code> se estiver ativa, <code>false</code> caso contrário.
     */
    private boolean isAtiva;

    public JustificativaAjuste() {

    }

    @NotNull
    public static JustificativaAjuste createDummy() {
        final JustificativaAjuste justificativa = new JustificativaAjuste();
        justificativa.setCodigo(10L);
        justificativa.setCodEmpresa(3L);
        justificativa.setNomeJustificativaAjuste("Esquecimento");
        justificativa.setObrigatorioObservacao(true);
        justificativa.setAtiva(true);
        return justificativa;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
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
                "codigo=" + codigo +
                ", codEmpresa=" + codEmpresa +
                ", nomeJustificativaAjuste='" + nomeJustificativaAjuste + '\'' +
                ", obrigatorioObservacao=" + obrigatorioObservacao +
                ", isAtiva=" + isAtiva +
                '}';
    }
}