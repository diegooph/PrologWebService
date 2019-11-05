package br.com.zalf.prolog.webservice.frota.pneu.servico._model;

import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;

/**
 * Created on 12/20/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class VeiculoServico extends Veiculo {

    /**
     * O KM do {@link Veiculo} quando o {@link Servico} foi aberto.
     */
    private int kmAberturaServico;

    public int getKmAberturaServico() {
        return kmAberturaServico;
    }

    public void setKmAberturaServico(int kmAberturaServico) {
        this.kmAberturaServico = kmAberturaServico;
    }
}