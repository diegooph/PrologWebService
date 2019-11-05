package br.com.zalf.prolog.webservice.frota.pneu.servico._model;

import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;

/**
 * Created by jean on 10/06/16.
 */
public final class ServicoInspecao extends ServicoCalibragem {

    private Alternativa alternativaSelecionada;

    public ServicoInspecao() {
        setTipoServico(TipoServico.INSPECAO);
    }

    public ServicoInspecao(Alternativa alternativaSelecionada) {
        this.alternativaSelecionada = alternativaSelecionada;
        setTipoServico(TipoServico.INSPECAO);
    }

    public Alternativa getAlternativaSelecionada() {
        return alternativaSelecionada;
    }

    public void setAlternativaSelecionada(Alternativa alternativaSelecionada) {
        this.alternativaSelecionada = alternativaSelecionada;
    }
}
