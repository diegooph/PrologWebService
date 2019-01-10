package br.com.zalf.prolog.webservice.frota.checklist.model;

import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;

import java.util.List;

/**
 * Created by jean on 20/04/16.
 */
public class NovoChecklistHolder {
    private List<PerguntaRespostaChecklist> listPerguntas;
    private Veiculo veiculo;
    /**
     * Código do modelo de checklist do qual as perguntas {@link #listPerguntas} são referentes.
     */
    private Long codigoModeloChecklist;

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

    public Long getCodigoModeloChecklist() {
        return codigoModeloChecklist;
    }

    public void setCodigoModeloChecklist(Long codigoModeloChecklist) {
        this.codigoModeloChecklist = codigoModeloChecklist;
    }

    @Override
    public String toString() {
        return "NovoChecklistHolder{" +
                "listPerguntas=" + listPerguntas +
                ", veiculo=" + veiculo +
                '}';
    }
}
