package br.com.zalf.prolog.webservice.frota.checklist.OLD;

import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    public static AlternativaChecklist create(@NotNull final Long codigo,
                                              @NotNull final String descricao,
                                              final boolean tipoOutros,
                                              final int ordemExibicao,
                                              @NotNull final PrioridadeAlternativa prioridade) {
        final AlternativaChecklist a = new AlternativaChecklist();
        a.codigo = codigo;
        a.alternativa = descricao;
        if (tipoOutros) {
            a.tipo = TIPO_OUTROS;
        }
        a.ordemExibicao = ordemExibicao;
        a.prioridade = prioridade;
        return a;
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
