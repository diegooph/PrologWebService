package br.com.zalf.prolog.webservice.frota.pneu.servico.model;

/**
 * Created on 12/4/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class QuantidadeServicosFechadosPneu extends QuantidadeServicosFechados {
    private String codigoPneu;

    public QuantidadeServicosFechadosPneu() {
        setAgrupamento(AgrupamentoServicosFechados.POR_PNEU);
    }

    public String getCodigoPneu() {
        return codigoPneu;
    }

    public void setCodigoPneu(String codigoPneu) {
        this.codigoPneu = codigoPneu;
    }
}