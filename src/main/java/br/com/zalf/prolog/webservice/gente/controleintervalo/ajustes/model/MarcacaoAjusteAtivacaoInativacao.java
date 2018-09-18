package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Classe utilizada quando uma marcação for ativada ou inativada, seja ela de início ou de fim.
 *
 * Created on 04/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class MarcacaoAjusteAtivacaoInativacao extends MarcacaoAjuste {
    /**
     * O código da marcação que será ativada ou inativada.
     */
    private Long codMarcacaoAtivacaoInativacao;

    /**
     * <code>boolean</code> indicando se a marcação deve ser ativada ou inativada. <code>true</code> para ativar
     * e <code>false</code> para inativar.
     */
    private boolean deveAtivar;

    public MarcacaoAjusteAtivacaoInativacao() {
        super(TipoMarcacaoAjuste.ATIVACAO_INATIVACAO);
    }

    @NotNull
    public static MarcacaoAjusteAtivacaoInativacao createDummy() {
        final MarcacaoAjusteAtivacaoInativacao ajusteAtivacaoInativacao = new MarcacaoAjusteAtivacaoInativacao();
        ajusteAtivacaoInativacao.setDeveAtivar(true);
        ajusteAtivacaoInativacao.setCodMarcacaoAtivacaoInativacao(20L);
        ajusteAtivacaoInativacao.setCodJustificativaAjuste(5L);
        ajusteAtivacaoInativacao.setObservacaoAjuste("Dummy Data");
        ajusteAtivacaoInativacao.setDataHoraAjuste(LocalDateTime.now());
        return ajusteAtivacaoInativacao;
    }

    public Long getCodMarcacaoAtivacaoInativacao() {
        return codMarcacaoAtivacaoInativacao;
    }

    public void setCodMarcacaoAtivacaoInativacao(final Long codMarcacaoAtivacaoInativacao) {
        this.codMarcacaoAtivacaoInativacao = codMarcacaoAtivacaoInativacao;
    }

    public boolean isDeveAtivar() {
        return deveAtivar;
    }

    public void setDeveAtivar(final boolean deveAtivar) {
        this.deveAtivar = deveAtivar;
    }

    @Override
    public String toString() {
        return "MarcacaoAjusteAtivacaoInativacao{" +
                "deveAtivar=" + deveAtivar +
                '}';
    }
}
