package br.com.zalf.prolog.webservice.frota.checklist.OLD;

import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;

/**
 * Created by Zalf on 07/01/17.
 */
@Deprecated
public class AlternativaChecklist extends Alternativa {

    public PrioridadeAlternativa prioridade;

    /**
     * Indica se a alternativa atual está marcada (selecionada) ou não.
     */
    public boolean selected;

    public AlternativaChecklist() {

    }

    public PrioridadeAlternativa getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(final PrioridadeAlternativa prioridade) {
        this.prioridade = prioridade;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "AlternativaChecklist{" +
                ", selected=" + selected +
                super.toString() +
                '}';
    }
}
