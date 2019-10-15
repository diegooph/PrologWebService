package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 2019-09-22
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PerguntaModeloChecklistEdicaoAtualiza extends PerguntaModeloChecklistEdicao {
    @NotNull
    private final Long codigo;
    @NotNull
    private final Long codigoContexto;
    @NotNull
    private final String descricao;
    @Nullable
    private final Long codImagem;
    private final int ordemExibicao;
    private final boolean singleChoice;
    @NotNull
    private final List<AlternativaModeloChecklistEdicao> alternativas;

    public PerguntaModeloChecklistEdicaoAtualiza(@NotNull final Long codigo,
                                                 @NotNull final Long codigoContexto,
                                                 @NotNull final String descricao,
                                                 @Nullable final Long codImagem,
                                                 final int ordemExibicao,
                                                 final boolean singleChoice,
                                                 @NotNull final List<AlternativaModeloChecklistEdicao> alternativas) {
        this.codigo = codigo;
        this.codigoContexto = codigoContexto;
        this.descricao = descricao;
        this.codImagem = codImagem;
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
