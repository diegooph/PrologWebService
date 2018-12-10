package br.com.zalf.prolog.webservice.frota.checklist.modelo.model;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.PerguntaModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.PerguntaModeloChecklistInsercao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public abstract class PerguntaModeloChecklist {
    private Long codigo;
    private String descricao;
    private Long codImagem;
    private String urlImagem;
    private int ordemExibicao;
    private boolean singleChoice;
    private List<AlternativaModeloChecklist> alternativas;

    @NotNull
    @Exclude
    private final String tipo;

    public PerguntaModeloChecklist(@NotNull final String tipo) {
        this.tipo = tipo;
    }

    @NotNull
    public static RuntimeTypeAdapterFactory<PerguntaModeloChecklist> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(PerguntaModeloChecklist.class, "tipo")
                .registerSubtype(PerguntaModeloChecklistInsercao.class, PerguntaModeloChecklistInsercao.TIPO_SERIALIZACAO)
                .registerSubtype(PerguntaModeloChecklistEdicao.class, PerguntaModeloChecklistEdicao.TIPO_SERIALIZACAO);
    }

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

    public Long getCodImagem() {
        return codImagem;
    }

    public void setCodImagem(final Long codImagem) {
        this.codImagem = codImagem;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(final String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public int getOrdemExibicao() {
        return ordemExibicao;
    }

    public void setOrdemExibicao(final int ordemExibicao) {
        this.ordemExibicao = ordemExibicao;
    }

    public boolean isSingleChoice() {
        return singleChoice;
    }

    public void setSingleChoice(final boolean singleChoice) {
        this.singleChoice = singleChoice;
    }

    public List<AlternativaModeloChecklist> getAlternativas() {
        return alternativas;
    }

    public void setAlternativas(final List<AlternativaModeloChecklist> alternativas) {
        this.alternativas = alternativas;
    }
}