package br.com.zalf.prolog.webservice.frota.checklist.model;

/**
 * Created on 11/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class AlternativaChecklistAbreOrdemServico {
    private Long codAlteranativa;
    private String descricao;
    private boolean tipoOutros;
    private boolean possuiItemPendente;
    private Long codOrdemServico;
    private Integer quantidadeApontamentos;

    public AlternativaChecklistAbreOrdemServico() {

    }

    public Long getCodAlteranativa() {
        return codAlteranativa;
    }

    public void setCodAlteranativa(final Long codAlteranativa) {
        this.codAlteranativa = codAlteranativa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(final String descricao) {
        this.descricao = descricao;
    }

    public boolean isTipoOutros() {
        return tipoOutros;
    }

    public void setTipoOutros(final boolean tipoOutros) {
        this.tipoOutros = tipoOutros;
    }

    public boolean isPossuiItemPendente() {
        return possuiItemPendente;
    }

    public void setPossuiItemPendente(final boolean possuiItemPendente) {
        this.possuiItemPendente = possuiItemPendente;
    }

    public Long getCodOrdemServico() {
        return codOrdemServico;
    }

    public void setCodOrdemServico(final Long codOrdemServico) {
        this.codOrdemServico = codOrdemServico;
    }

    public Integer getQuantidadeApontamentos() {
        return quantidadeApontamentos;
    }

    public void setQuantidadeApontamentos(final Integer quantidadeApontamentos) {
        this.quantidadeApontamentos = quantidadeApontamentos;
    }
}
