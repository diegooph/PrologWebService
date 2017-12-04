package br.com.zalf.prolog.webservice.frota.pneu.servico.model;


import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;

/**
 * Created on 12/4/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public abstract class QuantidadeServicosFechados {
    static final String AGRUPAMENTO_POR_PNEU = "POR_PNEU";
    static final String AGRUPAMENTO_POR_VEICULO = "POR_VEICULO";
    public int qtdServicosFechadosCalibragem;
    public int qtdServicosFechadosInspecao;
    public int qtdServicosFechadosMovimentacao;
    @Exclude
    public String agrupamento;

    public static RuntimeTypeAdapterFactory<QuantidadeServicosFechados> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(QuantidadeServicosFechados.class, "agrupamento")
                .registerSubtype(QuantidadeServicosFechadosPneu.class, AGRUPAMENTO_POR_PNEU)
                .registerSubtype(QuantidadeServicosFechadosVeiculo.class, AGRUPAMENTO_POR_VEICULO);
    }
}