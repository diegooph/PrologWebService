package br.com.zalf.prolog.webservice.frota.checklist.model;

import br.com.zalf.prolog.webservice.commons.questoes.Pergunta;

import java.util.List;

/**
 * Created by luiz on 4/16/16.
 */
public class PerguntaRespostaChecklist extends Pergunta {

    public static final String CRITICA = "CRITICA";
    public static final String ALTA = "ALTA";
    public static final String BAIXA = "BAIXA";

    private int ordemExibicao;
    private String url;
    private List<AlternativaChecklist> alternativasResposta;
    private boolean singleChoice;
    private String prioridade;

    public PerguntaRespostaChecklist(){

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

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
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

    @Override
    public String toString() {
        return "PerguntaRespostaChecklist{" +
                "ordemExibicao=" + ordemExibicao +
                ", url='" + url + '\'' +
                ", alternativasResposta=" + alternativasResposta +
                ", singleChoice=" + singleChoice +
                ", prioridade='" + prioridade + '\'' +
                super.toString() +
                '}';
    }
}