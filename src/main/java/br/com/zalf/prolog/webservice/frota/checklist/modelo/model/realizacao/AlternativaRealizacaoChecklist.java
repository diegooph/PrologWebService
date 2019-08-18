package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-08-17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AlternativaRealizacaoChecklist {
    /**
     * Código único de identificação da alternativa.
     */
    @NotNull
    private final Long codigo;

    /**
     * Atributo alfanumérico que representa o texto descritivo da alternativa.
     */
    @NotNull
    private final String descricao;

    /**
     * Valor booleano que representa se essa alternativa é do "tipo outros", caso <code>TRUE</code>.
     * "Tipo outros" é utilizado para a alternativa que solicita o input manual do problema apresentado pela
     * {@link PerguntaRealizacaoChecklist pergunta} a qual essa alternativa pertence.
     */
    private final boolean tipoOutros;

    /**
     * Atributo numérico que denota a ordem que essa alternativa será exibida na realização do checklist.
     */
    private final int ordemExibicao;

    /**
     * Prioridade associada a esta alternativa. A prioridade pode ser {@link PrioridadeAlternativa#CRITICA},
     * {@link PrioridadeAlternativa#ALTA} ou {@link PrioridadeAlternativa#BAIXA} e ela remete ao tempo que o item tem
     * para ser resolvido.
     */
    @NotNull
    private final PrioridadeAlternativa prioridadeAlternativa;

    public AlternativaRealizacaoChecklist(@NotNull final Long codigo,
                                          @NotNull final String descricao,
                                          final boolean tipoOutros,
                                          final int ordemExibicao,
                                          @NotNull final PrioridadeAlternativa prioridadeAlternativa) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.tipoOutros = tipoOutros;
        this.ordemExibicao = ordemExibicao;
        this.prioridadeAlternativa = prioridadeAlternativa;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getDescricao() {
        return descricao;
    }

    public boolean isTipoOutros() {
        return tipoOutros;
    }

    public int getOrdemExibicao() {
        return ordemExibicao;
    }

    @NotNull
    public PrioridadeAlternativa getPrioridadeAlternativa() {
        return prioridadeAlternativa;
    }
}
