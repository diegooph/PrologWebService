package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.item;

import br.com.zalf.prolog.webservice.frota.checklist.model.PerguntaRespostaChecklist;
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
     * Indica se essa alternativa é do tipo outros. Se for (true nesse caso), então o usuário
     * forneceu uma resposta durante a realização do checklist e não escolheu uma das pré definidas.
     */
    private boolean tipoOutros;

    /**
     * Se a alternativa for do tipo outros, então essa String conterá a descrição fornecida pelo
     * usuário, do contrário será <code>null</code>.
     */
    private String descricaoTipoOutros;

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
        alternativa.setTipoOutros(true);
        alternativa.setDescricaoTipoOutros("Descrição tipo outros");
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

    public boolean isTipoOutros() {
        return tipoOutros;
    }

    public void setTipoOutros(final boolean tipoOutros) {
        this.tipoOutros = tipoOutros;
    }

    public String getDescricaoTipoOutros() {
        return descricaoTipoOutros;
    }

    public void setDescricaoTipoOutros(final String descricaoTipoOutros) {
        this.descricaoTipoOutros = descricaoTipoOutros;
    }

    public PrioridadeAlternativa getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(final PrioridadeAlternativa prioridade) {
        this.prioridade = prioridade;
    }
}
