package br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.unidade._model.Unidade;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

/**
 * Created by luiz on 03/04/17.
 */
public class ProcessoMovimentacao {
    @Nullable
    private Long codigo;
    @NotNull
    private Unidade unidade;
    @NotNull
    private final List<Movimentacao> movimentacoes;
    @NotNull
    private final Colaborador colaborador;
    @NotNull
    private final Date data;
    @Nullable
    private final String observacao;

    public ProcessoMovimentacao(@Nullable Long codigo,
                                @NotNull Unidade unidade,
                                @NotNull List<Movimentacao> movimentacoes,
                                @NotNull Colaborador colaborador,
                                @NotNull Date data,
                                @Nullable String observacao) {
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

    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
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