package br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.destino.Destino;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.origem.Origem;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Zart on 23/02/17.
 */
public class Movimentacao {
    private Long codigo;
    private final Pneu pneu;
    private final Origem origem;
    private final Destino destino;
    private final String observacao;

    public Movimentacao(@Nullable Long codigo, @NotNull Pneu pneu, @NotNull Origem origem, @NotNull Destino destino,
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

    /**
     * Verifica se a movimentação é da origem fornecida.
     */
    public boolean isFrom(@NotNull final OrigemDestinoEnum origem) {
        return this.origem.getTipo().equals(origem);
    }

    /**
     * Verifica se a movimentação é para o destino fornecido.
     */
    public boolean isTo(@NotNull final OrigemDestinoEnum destino) {
        return this.destino.getTipo().equals(destino);
    }

    public boolean isFromDestinoToOrigem(@NotNull final OrigemDestinoEnum origem, @NotNull final OrigemDestinoEnum destino) {
        return getOrigem().getTipo().equals(origem) && getDestino().getTipo().equals(destino);
    }
}