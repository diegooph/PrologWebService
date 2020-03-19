package br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model;

import br.com.zalf.prolog.webservice.customfields.CampoPersonalizadoResposta;
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
    @Nullable
    private List<CampoPersonalizadoResposta> respostasCamposPersonalizados;

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

    @NotNull
    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(@NotNull final Unidade unidade) {
        this.unidade = unidade;
    }

    @Nullable
    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(@Nullable final Long codigo) {
        this.codigo = codigo;
    }

    @NotNull
    public List<Movimentacao> getMovimentacoes() {
        return movimentacoes;
    }

    @NotNull
    public Colaborador getColaborador() {
        return colaborador;
    }

    @NotNull
    public Date getData() {
        return data;
    }

    @Nullable
    public String getObservacao() {
        return observacao;
    }

    @Nullable
    public List<CampoPersonalizadoResposta> getRespostasCamposPersonalizados() {
        return respostasCamposPersonalizados;
    }
}