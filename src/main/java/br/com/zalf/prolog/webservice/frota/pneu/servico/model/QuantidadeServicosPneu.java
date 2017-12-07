package br.com.zalf.prolog.webservice.frota.pneu.servico.model;

/**
 * Created on 12/4/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class QuantidadeServicosPneu extends QuantidadeServicos {
    private String codigoPneu;

    public QuantidadeServicosPneu() {
        setAgrupamento(AgrupamentoQuantidadeServicos.POR_PNEU);
    }

    public String getCodigoPneu() {
        return codigoPneu;
    }

    public void setCodigoPneu(String codigoPneu) {
        this.codigoPneu = codigoPneu;
    }
}