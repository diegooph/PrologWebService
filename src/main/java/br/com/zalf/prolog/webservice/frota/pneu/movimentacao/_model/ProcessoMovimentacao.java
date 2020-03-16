package br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

/**
 * Created by luiz on 03/04/17.
 */
public class ProcessoMovimentacao {

    @NotNull
    private final List<Movimentacao> movimentacoes;
    @NotNull
    private final Colaborador colaborador;
    @NotNull
    private final Date data;
    @Nullable
    private final String observacao;
    @Nullable
    private Long codigo;
    @NotNull
    private Unidade unidade;

    public ProcessoMovimentacao(@Nullable final Long codigo,
                                @NotNull final Unidade unidade,
                                @NotNull final List<Movimentacao> movimentacoes,
                                @NotNull final Colaborador colaborador,
                                @NotNull final Date data,
                                @Nullable final String observacao) {
        this.codigo = codigo;
        this.unidade = unidade;
        this.movimentacoes = movimentacoes;
        this.colaborador = colaborador;
        this.data = data;
        this.observacao = observacao;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(final Unidade unidade) {
        this.unidade = unidade;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
    }

    public List<Movimentacao> getMovimentacoes() {
        return movimentacoes;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public Date getData() {
        return data;
    }

    public String getObservacao() {
        return observacao;
    }
}