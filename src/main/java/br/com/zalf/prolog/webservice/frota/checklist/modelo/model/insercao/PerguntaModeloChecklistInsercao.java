package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.PerguntaModeloChecklist;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 10/12/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PerguntaModeloChecklistInsercao extends PerguntaModeloChecklist {
    @NotNull
    private final String descricao;
    @Nullable
    private final Long codImagem;
    private final int ordemExibicao;
    private final boolean singleChoice;
    @NotNull
    private final List<AlternativaModeloChecklistInsercao> alternativas;


    public PerguntaModeloChecklistInsercao(@NotNull final String descricao,
                                           @Nullable final Long codImagem,
                                           final int ordemExibicao,
                                           final boolean singleChoice,
                                           @NotNull final List<AlternativaModeloChecklistInsercao> alternativas) {
        this.descricao = descricao;
        this.codImagem = codImagem;
        this.ordemExibicao = ordemExibicao;
        this.singleChoice = singleChoice;
        this.alternativas = alternativas;
    }

    @NotNull
    @Override
    public Long getCodigo() {
        return null;
    }

    @NotNull
    @Override
    public Long getCodigoContexto() {
        return null;
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
        return null;
    }

    @Override
    public int getOrdemExibicao() {
        return ordemExibicao;
    }

    @Override
    public boolean isSingleChoice() {
        return singleChoice;
    }

    @NotNull
    @Override
    public List<AlternativaModeloChecklist> getAlternativas() {
        //noinspection unchecked
        return (List<AlternativaModeloChecklist>) (List<?>) alternativas;
    }
}