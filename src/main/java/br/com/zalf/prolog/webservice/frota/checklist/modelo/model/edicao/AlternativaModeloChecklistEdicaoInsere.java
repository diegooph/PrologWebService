package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-09-22
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AlternativaModeloChecklistEdicaoInsere extends AlternativaModeloChecklistEdicao {
    @NotNull
    private final String descricao;
    @NotNull
    private final PrioridadeAlternativa prioridade;
    private final boolean tipoOutros;
    private final int ordemExibicao;
    private final boolean deveAbrirOrdemServico;

    public AlternativaModeloChecklistEdicaoInsere(@NotNull final String descricao,
                                                  @NotNull final PrioridadeAlternativa prioridade,
                                                  final boolean tipoOutros,
                                                  final int ordemExibicao,
                                                  final boolean deveAbrirOrdemServico) {
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.tipoOutros = tipoOutros;
        this.ordemExibicao = ordemExibicao;
        this.deveAbrirOrdemServico = deveAbrirOrdemServico;
    }

    @NotNull
    @Override
    public Long getCodigo() {
        throw new UnsupportedOperationException(AlternativaModeloChecklistEdicaoInsere.class.getSimpleName()
                + " não tem codigo");
    }

    @NotNull
    @Override
    public Long getCodigoContexto() {
        throw new UnsupportedOperationException(AlternativaModeloChecklistEdicaoInsere.class.getSimpleName()
                + " não tem codigoContexto");
    }

    @Override
    @NotNull
    public String getDescricao() {
        return descricao;
    }

    @Override
    @NotNull
    public PrioridadeAlternativa getPrioridade() {
        return prioridade;
    }

    @Override
    public boolean isTipoOutros() {
        return tipoOutros;
    }

    @Override
    public int getOrdemExibicao() {
        return ordemExibicao;
    }

    @Override
    public boolean isDeveAbrirOrdemServico() {
        return deveAbrirOrdemServico;
    }
}
