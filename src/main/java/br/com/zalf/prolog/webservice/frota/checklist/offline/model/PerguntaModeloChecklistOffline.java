package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 10/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PerguntaModeloChecklistOffline {
    /**
     * Código único de identificação da pergunta.
     */
    @NotNull
    private final Long codigo;

    /**
     * Alfanumérico que representa o texto descritivo da pergunta.
     */
    @NotNull
    private final String descricao;

    /**
     * Código único de identificação da imagem ilustrativa associada a essa pergunta.
     * Este atributo pode ser nulo para o caso de não ter nenhuma imagem associada à pergunta.
     */
    @Nullable
    private final Long codImagem;

    /**
     * Alfanumérico que representa o local onde esta imagem está disponível para ser acessada e baixada.
     * Este atributo pode ser nulo para o caso de não ter nenhuma imagem associada à pergunta.
     */
    @Nullable
    private final String urlImagem;

    /**
     * Atributo numérico que denota a ordem que essa pergunta será exibida na realização do checklist.
     */
    private final int ordemExibicao;

    /**
     * Atributo booleano que representa se a pergunta pode possuir uma única resposta, caso <code>TRUE</code>, ou
     * várias respostas, caso <code>FALSE</code>.
     */
    private final boolean singleChoice;

    /**
     * {@link AlternativaModeloChecklistOffline Alternativas} disponíveis para está pergunta.
     */
    @NotNull
    private final List<AlternativaModeloChecklistOffline> alternativas;

    PerguntaModeloChecklistOffline(@NotNull final Long codigo,
                                   @NotNull final String descricao,
                                   @Nullable final Long codImagem,
                                   @Nullable final String urlImagem,
                                   final int ordemExibicao,
                                   final boolean singleChoice,
                                   @NotNull final List<AlternativaModeloChecklistOffline> alternativas) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.codImagem = codImagem;
        this.urlImagem = urlImagem;
        this.ordemExibicao = ordemExibicao;
        this.singleChoice = singleChoice;
        this.alternativas = alternativas;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getDescricao() {
        return descricao;
    }

    @Nullable
    public Long getCodImagem() {
        return codImagem;
    }

    @Nullable
    public String getUrlImagem() {
        return urlImagem;
    }

    public int getOrdemExibicao() {
        return ordemExibicao;
    }

    public boolean isSingleChoice() {
        return singleChoice;
    }

    @NotNull
    public List<AlternativaModeloChecklistOffline> getAlternativas() {
        return alternativas;
    }
}