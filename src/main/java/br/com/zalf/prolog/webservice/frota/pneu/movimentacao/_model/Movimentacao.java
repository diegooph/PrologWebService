package br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model;

import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.destino.Destino;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.origem.Origem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Zart on 23/02/17.
 */
public class Movimentacao {
    private final Pneu pneu;
    private final Origem origem;
    private final Destino destino;
    private final String observacao;
    private final Long codMotivoMovimento;
    private Long codigo;

    public Movimentacao(@Nullable final Long codigo,
                        @NotNull final Pneu pneu,
                        @NotNull final Origem origem,
                        @NotNull final Destino destino,
                        @Nullable final String observacao,
                        @Nullable final Long codMotivoMovimento) {
        this.codigo = codigo;
        this.pneu = pneu;
        this.origem = origem;
        this.destino = destino;
        this.observacao = observacao;
        this.codMotivoMovimento = codMotivoMovimento;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
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

    public Long getCodMotivoMovimento() {
        return this.codMotivoMovimento;
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

    public boolean isFromOrigemToDestino(@NotNull final OrigemDestinoEnum origem, @NotNull final OrigemDestinoEnum destino) {
        return getOrigem().getTipo().equals(origem) && getDestino().getTipo().equals(destino);
    }
}