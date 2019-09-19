package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class AlternativaModeloChecklistVisualizacao extends AlternativaModeloChecklist {
    @NotNull
    private final Long codigo;
    @NotNull
    private final Long codigoFixo;
    @NotNull
    private final String descricao;
    @NotNull
    private final PrioridadeAlternativa prioridade;
    private final boolean tipoOutros;
    private final int ordemExibicao;
    private final boolean deveAbrirOrdemServico;

    public AlternativaModeloChecklistVisualizacao(@NotNull final Long codigo,
                                                  @NotNull final Long codigoFixo,
                                                  @NotNull final String descricao,
                                                  @NotNull final PrioridadeAlternativa prioridade,
                                                  final boolean tipoOutros,
                                                  final int ordemExibicao,
                                                  final boolean deveAbrirOrdemServico) {
        this.codigo = codigo;
        this.codigoFixo = codigoFixo;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.tipoOutros = tipoOutros;
        this.ordemExibicao = ordemExibicao;
        this.deveAbrirOrdemServico = deveAbrirOrdemServico;
    }

    @Override
    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @Override
    @NotNull
    public Long getCodigoFixo() {
        return codigoFixo;
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
