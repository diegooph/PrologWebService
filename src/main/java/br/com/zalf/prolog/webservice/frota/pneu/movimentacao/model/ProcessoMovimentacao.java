package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model;

import br.com.zalf.prolog.webservice.commons.colaborador.Colaborador;
import br.com.zalf.prolog.webservice.commons.colaborador.Unidade;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.Date;
import java.util.List;

/**
 * Created by luiz on 03/04/17.
 */
public class ProcessoMovimentacao {
    @NotNull
    private Unidade unidade;
    @Nullable
    private Long codigo;
    @NotNull
    private final List<Movimentacao> movimentacoes;
    @NotNull
    private final Colaborador colaborador;
    @NotNull
    private final Date data;
    @Nullable
    private final String observacao;

    public ProcessoMovimentacao(@Nullable Long codigo,
                                @NotNull List<Movimentacao> movimentacoes,
                                @NotNull Colaborador colaborador,
                                @NotNull Date data,
                                @Nullable String observacao) {
        this.codigo = codigo;
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