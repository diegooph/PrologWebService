package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.PerguntaModeloChecklist;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class PerguntaModeloChecklistVisualizacao extends PerguntaModeloChecklist {
    @NotNull
    private final Long codigo;
    @NotNull
    private final Long codigoContexto;
    @NotNull
    private final String descricao;
    @Nullable
    private final Long codImagem;
    @Nullable
    private final String urlImagem;
    private final int ordemExibicao;
    private final boolean singleChoice;
    @NotNull
    private final List<AlternativaModeloChecklistVisualizacao> alternativas;

    public PerguntaModeloChecklistVisualizacao(@NotNull final Long codigo,
                                               @NotNull final Long codigoContexto,
                                               @NotNull final String descricao,
                                               @Nullable final Long codImagem,
                                               @Nullable final String urlImagem,
                                               final int ordemExibicao,
                                               final boolean singleChoice,
                                               @NotNull final List<AlternativaModeloChecklistVisualizacao> alternativas) {
        this.codigo = codigo;
        this.codigoContexto = codigoContexto;
        this.descricao = descricao;
        this.codImagem = codImagem;
        this.urlImagem = urlImagem;
        this.ordemExibicao = ordemExibicao;
        this.singleChoice = singleChoice;
        this.alternativas = alternativas;
    }

    @NotNull
    @Override
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    @Override
    public Long getCodigoContexto() {
        return codigoContexto;
    }

    @NotNull
    @Override
    public String getDescricao() {
        return descricao;
    }

    @Nullable
    @Override
    public Long getCodImagem() {
        return codImagem;
    }

    @Nullable
    @Override
    public String getUrlImagem() {
        return urlImagem;
    }

    @Override
    public int getOrdemExibicao() {
        return ordemExibicao;
    }

    @Override
    public boolean isSingleChoice() {
        return singleChoice;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public List<AlternativaModeloChecklist> getAlternativas() {
        return (List<AlternativaModeloChecklist>) (List<?>) alternativas;
    }
}