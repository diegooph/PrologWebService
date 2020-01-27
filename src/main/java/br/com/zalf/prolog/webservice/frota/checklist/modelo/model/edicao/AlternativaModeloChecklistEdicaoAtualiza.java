package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-09-22
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AlternativaModeloChecklistEdicaoAtualiza extends AlternativaModeloChecklistEdicao {
    @NotNull
    private final Long codigo;
    @NotNull
    private final Long codigoContexto;
    @NotNull
    private final String descricao;
    @NotNull
    private final PrioridadeAlternativa prioridade;
    private final boolean tipoOutros;
    private final int ordemExibicao;
    private final boolean deveAbrirOrdemServico;

    public AlternativaModeloChecklistEdicaoAtualiza(@NotNull final Long codigo,
                                                    @NotNull final Long codigoContexto,
                                                    @NotNull final String descricao,
                                                    @NotNull final PrioridadeAlternativa prioridade,
                                                    final boolean tipoOutros,
                                                    final int ordemExibicao,
                                                    final boolean deveAbrirOrdemServico) {
        this.codigo = codigo;
        this.codigoContexto = codigoContexto;
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
    public Long getCodigoContexto() {
        return codigoContexto;
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
