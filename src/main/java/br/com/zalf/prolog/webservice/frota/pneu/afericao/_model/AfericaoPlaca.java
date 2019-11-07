package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by jean on 04/04/16.
 */
public final class AfericaoPlaca extends Afericao {
    /**
     * O {@link Veiculo veiculo} no qual a aferição foi realizada.
     */
    private Veiculo veiculo;


    /**
     * Na busca de uma aferição já realizada, para saber o KM do veículo no momento da aferição, devemos consultar este
     * atributo, ao inves do {@link Veiculo#kmAtual kmAtual} do veículo.
     */
    private long kmMomentoAfericao;

    public AfericaoPlaca() {
        super(TipoProcessoColetaAfericao.PLACA);
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public long getKmMomentoAfericao() {
        return kmMomentoAfericao;
    }

    public void setKmMomentoAfericao(long kmMomentoAfericao) {
        this.kmMomentoAfericao = kmMomentoAfericao;
    }

    @NotNull
    @Override
    public List<Pneu> getPneusAferidos() {
        return getVeiculo().getListPneus();
    }
}