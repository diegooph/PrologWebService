package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.origem;

import br.com.zalf.prolog.webservice.commons.veiculo.Veiculo;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.OrigemDestinoConstants;
import com.sun.istack.internal.NotNull;

/**
 * Created by Zart on 23/02/17.
 */
public final class OrigemVeiculo extends Origem {
    @NotNull
    private final Veiculo veiculo;
    private final int posicaoOrigemPneu;

    /**
     * Gson utiliza construtor vazio para fazer o deserializer, assim precisamos garantir que nesse construtor
     * o tipo também seja setado
     */
    public OrigemVeiculo() {
        super(OrigemDestinoConstants.VEICULO);
        this.veiculo = null;
        this.posicaoOrigemPneu = 0;
    }

    public OrigemVeiculo(@NotNull Veiculo veiculo,  int posicaoOrigemPneu) {
        super(OrigemDestinoConstants.VEICULO);
        this.veiculo = veiculo;
        this.posicaoOrigemPneu = posicaoOrigemPneu;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public int getPosicaoOrigemPneu() {
        return posicaoOrigemPneu;
    }
}