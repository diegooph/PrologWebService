package br.com.zalf.prolog.frota.checklist;

import br.com.zalf.prolog.commons.questoes.Alternativa;

/**
 * Created by Zalf on 07/01/17.
 */
public class AlternativaChecklist extends Alternativa {
    /**
     * Indica se a alternativa atual está marcada (selecionada) ou não.
     */
    public boolean selected;

    public AlternativaChecklist() {
    }

    @Override
    public String toString() {
        return "AlternativaChecklist{" +
                "selected=" + selected +
                super.toString() +
                '}';
    }
}
