package br.com.zalf.prolog.webservice.frota.checklist.offline.model;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AlternativaModeloChecklistOffline {
    /**
     * Código único de identificação da alternativa.
     */
    @NotNull
    private final Long codigo;

    /**
     * Código de contexto da alternativa.
     */
    @NotNull
    private final Long codigoContexto;

    /**
     * Atributo alfanumérico que representa o texto descritivo da alternativa.
     */
    @NotNull
    private final String descricao;

    /**
     * Valor booleano que representa se essa alternativa é do "tipo outros", caso <code>TRUE</code>.
     * "Tipo outros" é utilizado para a alternativa que solicita o input manual do problema apresentado pela
     * {@link PerguntaModeloChecklistOffline pergunta} a qual essa alternativa pertence.
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

    AlternativaModeloChecklistOffline(@NotNull final Long codigo,
                                      @NotNull final Long codigoContexto,
                                      @NotNull final String descricao,
                                      final boolean tipoOutros,
                                      final int ordemExibicao,
                                      @NotNull final PrioridadeAlternativa prioridadeAlternativa) {
        this.codigo = codigo;
        this.codigoContexto = codigoContexto;
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
    public Long getCodigoContexto() {
        return codigoContexto;
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