package br.com.zalf.prolog.webservice.frota.pneu.servico._model;

import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.Restricao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;

import java.util.List;

/**
 * Essa classe contém todas as informações necessárias para o fechamento de qualquer tipo de serviço
 * ({@link TipoServico}) bem como os serviços disponíveis para fechamento.
 *
 * Created by jean on 04/04/16.
 */
public class ServicoHolder {
    private List<Servico> servicos;
    private Restricao restricao;

    /**
     * Placa do {@link Veiculo} no qual os {@link #servicos} são baseados.
     */
    private String placaVeiculo;

    /**
     * Utilizado para os serviços de inspeção.
     */
    private List<Alternativa> alternativasInspecao;

    /**
     * Utilizado para os serviços de movimentação.
     */
    private List<Pneu> pneusDisponiveis;

    public ServicoHolder() {

    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public List<Alternativa> getAlternativasInspecao() {
        return alternativasInspecao;
    }

    public void setAlternativasInspecao(List<Alternativa> listAlternativaInspecao) {
        this.alternativasInspecao = listAlternativaInspecao;
    }

    public Restricao getRestricao() {
        return restricao;
    }

    public void setRestricao(Restricao restricao) {
        this.restricao = restricao;
    }

    public List<Pneu> getPneusDisponiveis() {
        return pneusDisponiveis;
    }

    public void setPneusDisponiveis(List<Pneu> pneusDisponiveis) {
        this.pneusDisponiveis = pneusDisponiveis;
    }

    public List<Servico> getServicos() {
        return servicos;
    }

    public void setServicos(List<Servico> listServicos) {
        this.servicos = listServicos;
    }

    @Override
    public String toString() {
        return "ServicoHolder{" +
                "servicos=" + servicos +
                ", restricao=" + restricao +
                ", placaVeiculo='" + placaVeiculo + '\'' +
                ", alternativasInspecao=" + alternativasInspecao +
                ", pneusDisponiveis=" + pneusDisponiveis +
                '}';
    }
}