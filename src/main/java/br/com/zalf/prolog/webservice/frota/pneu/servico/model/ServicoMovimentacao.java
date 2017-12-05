package br.com.zalf.prolog.webservice.frota.pneu.servico.model;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;

/**
 * Created by jean on 04/04/16.
 */
public final class ServicoMovimentacao extends Servico {
    private Pneu pneuNovo;

    public ServicoMovimentacao(Pneu pneuNovo) {
        this.pneuNovo = pneuNovo;
        setTipoServico(TipoServico.MOVIMENTACAO);
    }

    public ServicoMovimentacao() {
        setTipoServico(TipoServico.MOVIMENTACAO);
    }

    public Pneu getPneuNovo() {
        return pneuNovo;
    }

    public void setPneuNovo(Pneu pneuNovo) {
        this.pneuNovo = pneuNovo;
    }
}