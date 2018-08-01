package br.com.zalf.prolog.webservice.frota.checklist.model;

import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;

/**
 * Created by Zalf on 07/01/17.
 */
public class AlternativaChecklist extends Alternativa {

    /**
     * Atributo restrito a ser apenas {@code DELETADA}/{@code ALTERADA}/{@code CRIADA}
     */
    public AcaoEdicaoAlternativa acaoEdicao;

    /**
     * Indica se a alternativa atual está marcada (selecionada) ou não.
     */
    public boolean selected;

    public AlternativaChecklist() {
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
