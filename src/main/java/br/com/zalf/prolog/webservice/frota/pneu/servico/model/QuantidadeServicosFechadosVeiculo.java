package br.com.zalf.prolog.webservice.frota.pneu.servico.model;

/**
 * Created on 12/4/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class QuantidadeServicosFechadosVeiculo extends QuantidadeServicosFechados {
    public String placaVeiculo;

    public QuantidadeServicosFechadosVeiculo() {
        agrupamento = AGRUPAMENTO_POR_VEICULO;
    }
}