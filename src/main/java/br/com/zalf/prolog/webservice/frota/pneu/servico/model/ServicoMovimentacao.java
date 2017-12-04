package br.com.zalf.prolog.webservice.frota.pneu.servico.model;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;

/**
 * Created by jean on 04/04/16.
 */
public class ServicoMovimentacao extends Servico {

    private Pneu pneuNovo;
    private String destinoPneu;


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

    public String getDestinoPneu() {
        return destinoPneu;
    }

    public void setDestinoPneu(String destinoPneu) {
        this.destinoPneu = destinoPneu;
    }

    @Override
    public String toString() {
        return "ServicoMovimentacao{" +
                "pneuNovo=" + pneuNovo +
                ", destinoPneu='" + destinoPneu + '\'' +
                '}';
    }
}
