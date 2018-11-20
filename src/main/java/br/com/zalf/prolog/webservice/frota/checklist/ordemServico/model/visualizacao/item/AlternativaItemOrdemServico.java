package br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.visualizacao.item;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AlternativaItemOrdemServico {
    private Long codAlteranativa;
    private String descricao;
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
