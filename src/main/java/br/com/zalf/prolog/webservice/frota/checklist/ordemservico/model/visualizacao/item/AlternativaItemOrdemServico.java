package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.item;

import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import org.jetbrains.annotations.NotNull;

/**
 * Classe que representa a alternativa de uma {@link PerguntaRespostaChecklist}
 * marcada como Não Ok (NOK) de um Item de uma Ordem de Serviço.
 *
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AlternativaItemOrdemServico {
    /**
     * Código da alternativa marcada como Não Ok (NOK) em uma {@link PerguntaRespostaChecklist}.
     */
    private Long codAlteranativa;

    /**
     * Descrição da alternativa marcada como Não Ok (NOK).
     */
    private String descricao;

    /**
     * Prioridade que a alternativa marcada como Não Ok (NOK) possui.
     * A prioridade pode ser:
     * *{@link PrioridadeAlternativa#CRITICA}.
     * *{@link PrioridadeAlternativa#ALTA}.
     * *{@link PrioridadeAlternativa#BAIXA}.
     */
    private PrioridadeAlternativa prioridade;

    public AlternativaItemOrdemServico() {

    }

    @NotNull
    public static AlternativaItemOrdemServico createDummy() {
        final AlternativaItemOrdemServico alternativa = new AlternativaItemOrdemServico();
        alternativa.setCodAlteranativa(24345L);
        alternativa.setDescricao("Teste Alternativa");
        alternativa.setPrioridade(PrioridadeAlternativa.CRITICA);
        return alternativa;
    }

    public Long getCodAlteranativa() {
        return codAlteranativa;
    }

    public void setCodAlteranativa(final Long codAlteranativa) {
        this.codAlteranativa = codAlteranativa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(final String descricao) {
        this.descricao = descricao;
    }

    public PrioridadeAlternativa getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(final PrioridadeAlternativa prioridade) {
        this.prioridade = prioridade;
    }
}
