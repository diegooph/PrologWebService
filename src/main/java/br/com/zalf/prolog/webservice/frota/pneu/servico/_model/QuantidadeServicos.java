package br.com.zalf.prolog.webservice.frota.pneu.servico._model;


import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;

/**
 * Created on 12/4/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public abstract class QuantidadeServicos {
    private int qtdServicosCalibragem;
    private int qtdServicosInspecao;
    private int qtdServicosMovimentacao;
    @Exclude
    private AgrupamentoQuantidadeServicos agrupamento;

    public static RuntimeTypeAdapterFactory<QuantidadeServicos> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(QuantidadeServicos.class, "agrupamento")
                .registerSubtype(QuantidadeServicosPneu.class, AgrupamentoQuantidadeServicos.POR_PNEU.asString())
                .registerSubtype(QuantidadeServicosVeiculo.class, AgrupamentoQuantidadeServicos.POR_VEICULO.asString());
    }

    public int getQtdServicosCalibragem() {
        return qtdServicosCalibragem;
    }

    public void setQtdServicosCalibragem(int qtdServicosCalibragem) {
        this.qtdServicosCalibragem = qtdServicosCalibragem;
    }

    public int getQtdServicosInspecao() {
        return qtdServicosInspecao;
    }

    public void setQtdServicosInspecao(int qtdServicosInspecao) {
        this.qtdServicosInspecao = qtdServicosInspecao;
    }

    public int getQtdServicosMovimentacao() {
        return qtdServicosMovimentacao;
    }

    public void setQtdServicosMovimentacao(int qtdServicosMovimentacao) {
        this.qtdServicosMovimentacao = qtdServicosMovimentacao;
    }

    void setAgrupamento(AgrupamentoQuantidadeServicos agrupamento) {
        this.agrupamento = agrupamento;
    }
}