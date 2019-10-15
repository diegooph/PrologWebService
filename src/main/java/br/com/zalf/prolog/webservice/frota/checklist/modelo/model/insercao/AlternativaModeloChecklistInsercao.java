package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/12/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AlternativaModeloChecklistInsercao extends AlternativaModeloChecklist {
    @NotNull
    private final String descricao;
    @NotNull
    private final PrioridadeAlternativa prioridade;
    private final boolean tipoOutros;
    private final int ordemExibicao;
    private final boolean deveAbrirOrdemServico;

    public AlternativaModeloChecklistInsercao(@NotNull final String descricao,
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
        throw new UnsupportedOperationException("Uma alternativa de inserção não tem código");
    }

    @NotNull
    @Override
    public Long getCodigoContexto() {
        throw new UnsupportedOperationException("Uma alternativa de inserção não tem código contexto");
    }

    @NotNull
    @Override
    public String getDescricao() {
        return descricao;
    }

    @NotNull
    @Override
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