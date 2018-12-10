package br.com.zalf.prolog.webservice.frota.checklist.OLD;

import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.AcaoEdicaoAlternativa;

/**
 * Created by Zalf on 07/01/17.
 */
@Deprecated
public class AlternativaChecklist extends Alternativa {

    /**
     * Atributo restrito a ser apenas {@code DELETADA}/{@code ALTERADA}/{@code CRIADA}
     */
    public AcaoEdicaoAlternativa acaoEdicao;

    public PrioridadeAlternativa prioridade;

    /**
     * Indica se a alternativa atual está marcada (selecionada) ou não.
     */
    public boolean selected;

    public AlternativaChecklist() {

    }

    public AcaoEdicaoAlternativa getAcaoEdicao() {
        return acaoEdicao;
    }

    public void setAcaoEdicao(final AcaoEdicaoAlternativa acaoEdicao) {
        this.acaoEdicao = acaoEdicao;
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
                "acaoEdicao='" + acaoEdicao + '\'' +
                ", selected=" + selected +
                super.toString() +
                '}';
    }
}
