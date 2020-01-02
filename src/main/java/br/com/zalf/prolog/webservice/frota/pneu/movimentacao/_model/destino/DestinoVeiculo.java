package br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.destino;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Zart on 24/02/17.
 */
public final class DestinoVeiculo extends Destino {
    @NotNull
    private final Veiculo veiculo;
    private final int posicaoDestinoPneu;

    /**
     * Gson utiliza construtor vazio para fazer o deserializer, assim precisamos garantir que nesse construtor
     * o tipo também seja setado
     */
    public DestinoVeiculo() {
        super(OrigemDestinoEnum.VEICULO);
        this.veiculo = null;
        this.posicaoDestinoPneu = 0;
    }

    public DestinoVeiculo(@NotNull Veiculo veiculo,  int posicaoDestinoPneu) {
        super(OrigemDestinoEnum.VEICULO);
        this.veiculo = veiculo;
        this.posicaoDestinoPneu = posicaoDestinoPneu;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public Integer getPosicaoDestinoPneu() {
        return posicaoDestinoPneu;
    }
}