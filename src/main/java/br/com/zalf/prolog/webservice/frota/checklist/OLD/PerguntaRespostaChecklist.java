package br.com.zalf.prolog.webservice.frota.checklist.OLD;

import br.com.zalf.prolog.webservice.commons.questoes.Pergunta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by luiz on 4/16/16.
 */
@Deprecated
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

    public PerguntaRespostaChecklist() {

    }

    @NotNull
    public static PerguntaRespostaChecklist create(@NotNull final Long codigo,
                                                   @NotNull final String descricao,
                                                   @Nullable final Long codImagem,
                                                   @Nullable final String urlImagem,
                                                   final int ordemExibicao,
                                                   final boolean singleChoice,
                                                   @NotNull final List<AlternativaChecklist> alternativas) {
        final PerguntaRespostaChecklist p = new PerguntaRespostaChecklist();
        p.setCodigo(codigo);
        p.setPergunta(descricao);
        p.codImagem = codImagem;
        p.url = urlImagem;
        p.ordemExibicao = ordemExibicao;
        p.singleChoice = singleChoice;
        p.alternativasResposta = alternativas;
        return p;
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

    @Override
    public String toString() {
        return "PerguntaRespostaChecklist{" +
                "ordemExibicao=" + ordemExibicao +
                ", codImagem=" + codImagem +
                ", url='" + url + '\'' +
                ", alternativasResposta=" + alternativasResposta +
                ", singleChoice=" + singleChoice +
                '}';
    }
}