package br.com.zalf.prolog.webservice.frota.checklist.model;

import br.com.zalf.prolog.webservice.frota.veiculo.Veiculo;

import java.util.List;

/**
 * Created by jean on 20/04/16.
 */
public class NovoChecklistHolder {
    private List<PerguntaRespostaChecklist> listPerguntas;
    private Veiculo veiculo;

    public NovoChecklistHolder() {
    }

    public List<PerguntaRespostaChecklist> getListPerguntas() {
        return listPerguntas;
    }

    public void setListPerguntas(List<PerguntaRespostaChecklist> listPerguntas) {
        this.listPerguntas = listPerguntas;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    @Override
    public String toString() {
        return "NovoChecklistHolder{" +
                "listPerguntas=" + listPerguntas +
                ", veiculo=" + veiculo +
                '}';
    }
}
