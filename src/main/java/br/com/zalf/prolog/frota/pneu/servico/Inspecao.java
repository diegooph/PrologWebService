package br.com.zalf.prolog.frota.pneu.servico;

import br.com.zalf.prolog.commons.questoes.Alternativa;

/**
 * Created by jean on 10/06/16.
 */
public class Inspecao extends Servico {

    private Alternativa alternativaSelecionada;

    public Inspecao() {
        setTipo(Servico.TIPO_INSPECAO);
    }

    public Inspecao(Alternativa alternativaSelecionada) {
        this.alternativaSelecionada = alternativaSelecionada;
        setTipo(Servico.TIPO_INSPECAO);
    }

    public Alternativa getAlternativaSelecionada() {
        return alternativaSelecionada;
    }

    public void setAlternativaSelecionada(Alternativa alternativaSelecionada) {
        this.alternativaSelecionada = alternativaSelecionada;
    }
}
