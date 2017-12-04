package br.com.zalf.prolog.webservice.frota.pneu.servico.model;

/**
 * Created on 12/4/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class QuantidadeServicosFechadosPneu extends QuantidadeServicosFechados {
    public String codigoPneu;

    public QuantidadeServicosFechadosPneu() {
        agrupamento = AGRUPAMENTO_POR_PNEU;
    }
}