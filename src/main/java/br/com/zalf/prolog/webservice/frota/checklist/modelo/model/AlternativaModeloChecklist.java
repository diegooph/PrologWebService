package br.com.zalf.prolog.webservice.frota.checklist.modelo.model;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.AcaoEdicaoAlternativa;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class AlternativaModeloChecklist {
    private Long codigo;
    private String descricao;
    private boolean tipoOutros;
    private int ordemExibicao;
    private PrioridadeAlternativa prioridade;

    /**
     * Quando um modelo de checklist é editado, indica qual foi a operação de edição realizada nessa alternativa.
     */
    private AcaoEdicaoAlternativa acaoEdicao;

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
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

    public int getOrdemExibicao() {
        return ordemExibicao;
    }

    public void setOrdemExibicao(final int ordemExibicao) {
        this.ordemExibicao = ordemExibicao;
    }

    public PrioridadeAlternativa getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(final PrioridadeAlternativa prioridade) {
        this.prioridade = prioridade;
    }

    public AcaoEdicaoAlternativa getAcaoEdicao() {
        return acaoEdicao;
    }

    public void setAcaoEdicao(final AcaoEdicaoAlternativa acaoEdicao) {
        this.acaoEdicao = acaoEdicao;
    }
}
