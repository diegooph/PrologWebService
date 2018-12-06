package br.com.zalf.prolog.webservice.frota.checklist.modelo.model;

import java.util.List;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class PerguntaModeloChecklist {
    private Long codigo;
    private String descricao;
    private Long codImagem;
    private String urlImagem;
    private int ordemExibicao;
    private boolean singleChoice;
    private List<AlternativaModeloChecklist> alternativas;

    /**
     * Quando um modelo de checklist é editado, indica qual foi a operação de edição realizada nessa pergunta.
     */
    private AcaoEdicaoPergunta acaoEdicao;

    public PerguntaModeloChecklist() {

    }

    public Long getCodImagem() {
        return codImagem;
    }

    public void setCodImagem(final Long codImagem) {
        this.codImagem = codImagem;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public int getOrdemExibicao() {
        return ordemExibicao;
    }

    public void setOrdemExibicao(int ordemExibicao) {
        this.ordemExibicao = ordemExibicao;
    }

    public List<AlternativaModeloChecklist> getAlternativas() {
        return alternativas;
    }

    public void setAlternativas(List<AlternativaModeloChecklist> alternativas) {
        this.alternativas = alternativas;
    }

    public boolean isSingleChoice() {
        return singleChoice;
    }

    public void setSingleChoice(boolean singleChoice) {
        this.singleChoice = singleChoice;
    }

    public AcaoEdicaoPergunta getAcaoEdicao() {
        return acaoEdicao;
    }

    public void setAcaoEdicao(final AcaoEdicaoPergunta acaoEdicao) {
        this.acaoEdicao = acaoEdicao;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}