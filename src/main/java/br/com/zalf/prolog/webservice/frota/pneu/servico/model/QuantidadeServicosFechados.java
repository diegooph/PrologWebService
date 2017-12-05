package br.com.zalf.prolog.webservice.frota.pneu.servico.model;


import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;

/**
 * Created on 12/4/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public abstract class QuantidadeServicosFechados {
    private int qtdServicosFechadosCalibragem;
    private int qtdServicosFechadosInspecao;
    private int qtdServicosFechadosMovimentacao;
    @Exclude
    private AgrupamentoServicosFechados agrupamento;

    public static RuntimeTypeAdapterFactory<QuantidadeServicosFechados> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(QuantidadeServicosFechados.class, "agrupamento")
                .registerSubtype(QuantidadeServicosFechadosPneu.class, AgrupamentoServicosFechados.POR_PNEU.asString())
                .registerSubtype(QuantidadeServicosFechadosVeiculo.class, AgrupamentoServicosFechados.POR_VEICULO.asString());
    }

    public int getQtdServicosFechadosCalibragem() {
        return qtdServicosFechadosCalibragem;
    }

    public void setQtdServicosFechadosCalibragem(int qtdServicosFechadosCalibragem) {
        this.qtdServicosFechadosCalibragem = qtdServicosFechadosCalibragem;
    }

    public int getQtdServicosFechadosInspecao() {
        return qtdServicosFechadosInspecao;
    }

    public void setQtdServicosFechadosInspecao(int qtdServicosFechadosInspecao) {
        this.qtdServicosFechadosInspecao = qtdServicosFechadosInspecao;
    }

    public int getQtdServicosFechadosMovimentacao() {
        return qtdServicosFechadosMovimentacao;
    }

    public void setQtdServicosFechadosMovimentacao(int qtdServicosFechadosMovimentacao) {
        this.qtdServicosFechadosMovimentacao = qtdServicosFechadosMovimentacao;
    }

    void setAgrupamento(AgrupamentoServicosFechados agrupamento) {
        this.agrupamento = agrupamento;
    }
}