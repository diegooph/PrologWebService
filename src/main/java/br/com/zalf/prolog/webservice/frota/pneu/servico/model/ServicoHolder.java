package br.com.zalf.prolog.webservice.frota.pneu.servico.model;

import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;

import java.util.List;

/**
 * Created by jean on 04/04/16.
 */
public class ServicoHolder {

    private List<Servico> listServicos;
    private List<Alternativa> listAlternativaInspecao;
    private List<Pneu> pneusDisponiveis;
    private Restricao restricao;
    /**
     * Veículo em cima do qual esse serviço é baseado
     */
    private Veiculo veiculo;

    public ServicoHolder() {
    }

    public List<Alternativa> getListAlternativaInspecao() {
        return listAlternativaInspecao;
    }

    public void setListAlternativaInspecao(List<Alternativa> listAlternativaInspecao) {
        this.listAlternativaInspecao = listAlternativaInspecao;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
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

    public List<Servico> getListServicos() {
        return listServicos;
    }

    public void setListServicos(List<Servico> listServicos) {
        this.listServicos = listServicos;
    }

    @Override
    public String toString() {
        return "ServicoHolder{" +
                "listServicos=" + listServicos +
                ", listAlternativaInspecao=" + listAlternativaInspecao +
                ", pneusDisponiveis=" + pneusDisponiveis +
                ", restricao=" + restricao +
                ", veiculo=" + veiculo +
                '}';
    }
}