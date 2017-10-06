package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.destino.Destino;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.origem.Origem;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

/**
 * Created by Zart on 23/02/17.
 */
public class Movimentacao {

    @Nullable
    private Long codigo;
    @NotNull
    private final Pneu pneu;
    @NotNull
    private final Origem origem;
    @NotNull
    private final Destino destino;
    @Nullable
    private final String observacao;

    public Movimentacao(@NotNull Long codigo, @NotNull Pneu pneu, @NotNull Origem origem, @NotNull Destino destino,
                        @Nullable String observacao) {
        this.codigo = codigo;
        this.pneu = pneu;
        this.origem = origem;
        this.destino = destino;
        this.observacao = observacao;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Pneu getPneu() {
        return pneu;
    }

    public Origem getOrigem() {
        return origem;
    }

    public Destino getDestino() {
        return destino;
    }

    public String getObservacao() {
        return observacao;
    }

    public boolean isFromDestinoToOrigem(@NotNull final String origem, @NotNull final String destino) {
        return getOrigem().getTipo().equals(origem) && getDestino().getTipo().equals(destino);
    }
}