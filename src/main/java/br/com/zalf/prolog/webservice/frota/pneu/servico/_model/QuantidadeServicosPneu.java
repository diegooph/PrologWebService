package br.com.zalf.prolog.webservice.frota.pneu.servico._model;

/**
 * Created on 12/4/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class QuantidadeServicosPneu extends QuantidadeServicos {
    private Long codigoPneu;
    private String codigoPneuCliente;

    public QuantidadeServicosPneu() {
        setAgrupamento(AgrupamentoQuantidadeServicos.POR_PNEU);
    }

    public Long getCodigoPneu() {
        return codigoPneu;
    }

    public void setCodigoPneu(Long codigoPneu) {
        this.codigoPneu = codigoPneu;
    }

    public String getCodigoPneuCliente() {
        return codigoPneuCliente;
    }

    public void setCodigoPneuCliente(final String codigoPneuCliente) {
        this.codigoPneuCliente = codigoPneuCliente;
    }
}