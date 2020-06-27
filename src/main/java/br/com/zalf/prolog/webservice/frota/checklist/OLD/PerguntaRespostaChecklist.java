package br.com.zalf.prolog.webservice.frota.checklist.OLD;

import br.com.zalf.prolog.webservice.commons.questoes.Pergunta;
import br.com.zalf.prolog.webservice.frota.checklist.model.MidiaResposta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
    @Nullable
    private List<MidiaResposta> midias;

    public PerguntaRespostaChecklist() {

    }

    public PerguntaRespostaChecklist(final Long codigo, final String pergunta, final String tipo) {
        super(codigo, pergunta, tipo);
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

    @NotNull
    public AlternativaChecklist addAlternativa(@NotNull final AlternativaChecklist alternativa) {
        alternativasResposta.add(alternativa);
        return alternativa;
    }

    public boolean temMidia(@NotNull final String uuidMidia) {
        return midias != null && midias
                .stream()
                .anyMatch(midiaResposta -> midiaResposta.getTipoMidia().equals(uuidMidia));
    }

    public void addMidia(@NotNull final MidiaResposta midia) {
        if (midias == null) {
            midias = new ArrayList<>();
        }

        midias.add(midia);
    }

    @Nullable
    public AlternativaChecklist getUltimaAlternativa() {
        if (alternativasResposta != null && alternativasResposta.size() > 0) {
            return alternativasResposta.get(alternativasResposta.size() - 1);
        }

        return null;
    }

    /**
     * @return {@code true} caso o usuário tenha respondido como OK essa pergunta; {@code false} caso contrário.
     */
    public boolean respondeuOk() {
        for (final AlternativaChecklist alternativaChecklist : alternativasResposta) {
            if (alternativaChecklist.selected) {
                return false;
            }
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

    public void setUrl(final String url) {
        this.url = url;
    }

    public int getOrdemExibicao() {
        return ordemExibicao;
    }

    public void setOrdemExibicao(final int ordemExibicao) {
        this.ordemExibicao = ordemExibicao;
    }

    public List<AlternativaChecklist> getAlternativasResposta() {
        return alternativasResposta;
    }

    public void setAlternativasResposta(final List<AlternativaChecklist> alternativasResposta) {
        this.alternativasResposta = alternativasResposta;
    }

    public boolean isSingleChoice() {
        return singleChoice;
    }

    public void setSingleChoice(final boolean singleChoice) {
        this.singleChoice = singleChoice;
    }

    public List<MidiaResposta> getMidias() { return midias; }

    public void setMidias(final List<MidiaResposta> midias) {
        this.midias = midias;
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