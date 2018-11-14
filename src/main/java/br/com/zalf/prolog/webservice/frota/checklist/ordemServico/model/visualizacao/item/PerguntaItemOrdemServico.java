package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.visualizacao.item;

/**
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PerguntaItemOrdemServico {
    private Long codPergunta;
    private String descricao;
    private AlternativaItemOrdemServico alternativaMarcada;

    public PerguntaItemOrdemServico() {

    }

    public Long getCodPergunta() {
        return codPergunta;
    }

    public void setCodPergunta(final Long codPergunta) {
        this.codPergunta = codPergunta;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(final String descricao) {
        this.descricao = descricao;
    }

    public AlternativaItemOrdemServico getAlternativaMarcada() {
        return alternativaMarcada;
    }

    public void setAlternativaMarcada(final AlternativaItemOrdemServico alternativaMarcada) {
        this.alternativaMarcada = alternativaMarcada;
    }
}