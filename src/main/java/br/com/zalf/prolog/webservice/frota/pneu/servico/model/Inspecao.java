package br.com.zalf.prolog.webservice.frota.pneu.servico.model;

import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;

/**
 * Created by jean on 10/06/16.
 */
public class Inspecao extends Servico {

    private Alternativa alternativaSelecionada;

    public Inspecao() {
        setTipoServico(TipoServico.INSPECAO);
    }

    public Inspecao(Alternativa alternativaSelecionada) {
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
