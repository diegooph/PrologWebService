package br.com.zalf.prolog.webservice.frota.checklist.model;

import br.com.zalf.prolog.webservice.commons.questoes.Pergunta;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.AcaoEdicaoPergunta;

import java.util.List;

/**
 * Created by luiz on 4/16/16.
 */
public class PerguntaRespostaChecklist extends Pergunta {

    /**
     * Criticidade que a pergunta pode ter, selecionada na criação do checklist
     */
    public static final String CRITICA = "CRITICA";
    public static final String ALTA = "ALTA";
    public static final String BAIXA = "BAIXA";

    private int ordemExibicao;
    private Long codImagem;
    private String url;
    private List<AlternativaChecklist> alternativasResposta;
    private boolean singleChoice;
    private AcaoEdicaoPergunta acaoEdicao;

    public PerguntaRespostaChecklist() {

    }

    /**
     *
     * @return {@code true} caso o usuário tenha respondido como OK essa pergunta; {@code false} caso contrário.
     */
    public boolean respondeuOk() {
        for (AlternativaChecklist alternativaChecklist : alternativasResposta) {
            if (alternativaChecklist.selected)
                return false;
        }

        return true;
    }

    public Long getCodImagem() {
        return codImagem;
    }

    public void setCodImagem(final Long codImagem) {
        this.codImagem = codImagem;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public PerguntaRespostaChecklist(Long codigo, String pergunta, String tipo) {
        super(codigo, pergunta, tipo);
    }

    public int getOrdemExibicao() {
        return ordemExibicao;
    }

    public void setOrdemExibicao(int ordemExibicao) {
        this.ordemExibicao = ordemExibicao;
    }

    public List<AlternativaChecklist> getAlternativasResposta() {
        return alternativasResposta;
    }

    public void setAlternativasResposta(List<AlternativaChecklist> alternativasResposta) {
        this.alternativasResposta = alternativasResposta;
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

    @Override
    public String toString() {
        return "PerguntaRespostaChecklist{" +
                "ordemExibicao=" + ordemExibicao +
                ", codImagem=" + codImagem +
                ", url='" + url + '\'' +
                ", alternativasResposta=" + alternativasResposta +
                ", singleChoice=" + singleChoice +
                ", acaoEdicao='" + acaoEdicao + '\'' +
                '}';
    }
}