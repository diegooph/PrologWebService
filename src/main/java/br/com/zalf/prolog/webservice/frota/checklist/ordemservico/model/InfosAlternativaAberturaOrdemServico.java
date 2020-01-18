package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Essa classe contém informações úteis durante o processamento de um checklist, para sabermos se abrimos ou não O.S.
 * ou se incrementamos a quantidade de apontamentos de um item já criado.
 * <p>
 * É importante ressaltar que apesar de nosso padrão de usar objetos para representar códigos ao invés de tipos
 * primitivos ({@link Long} ao invés de <code>long</code>), nesse objeto usaremos tipos primitivos.
 * Isso porque esse objeto será usado apenas no servidor então não corremos o risco de serializar os valores default do
 * Java. Além disso, iremos economizar memória.
 * <p>
 * Como essa implementação é uma parte crítica do sistema, optamos obrigar o uso do construtor para setar os atributos,
 * assim conseguimos realizar algumas validações no momento da criação.
 * <p>
 * Created on 11/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class InfosAlternativaAberturaOrdemServico {
    private final long codAlternativa;
    private final long codContextoPergunta;
    private final long codContextoAlternativa;
    private final long codItemOrdemServico;
    @Nullable
    private final String respostaTipoOutrosAberturaItem;
    private final int qtdApontamentosItem;
    private final boolean deveAbrirOrdemServico;
    private final boolean alternativaTipoOutros;
    @NotNull
    private final PrioridadeAlternativa prioridadeAlternativa;

    public InfosAlternativaAberturaOrdemServico(final long codAlternativa,
                                                final long codContextoPergunta,
                                                final long codContextoAlternativa,
                                                final long codItemOrdemServico,
                                                @Nullable final String respostaTipoOutrosAberturaItem,
                                                final int qtdApontamentosItem,
                                                final boolean deveAbrirOrdemServico,
                                                final boolean alternativaTipoOutros,
                                                @NotNull final PrioridadeAlternativa prioridadeAlternativa) {
        this.codAlternativa = codAlternativa;
        this.codContextoPergunta = codContextoPergunta;
        this.codContextoAlternativa = codContextoAlternativa;
        this.codItemOrdemServico = codItemOrdemServico;
        this.respostaTipoOutrosAberturaItem = respostaTipoOutrosAberturaItem;
        this.qtdApontamentosItem = qtdApontamentosItem;
        this.deveAbrirOrdemServico = deveAbrirOrdemServico;
        this.alternativaTipoOutros = alternativaTipoOutros;
        this.prioridadeAlternativa = prioridadeAlternativa;

        if (codItemOrdemServico > 0 && qtdApontamentosItem <= 0) {
            throw new IllegalStateException(String.format("O item de código %d não pode ter uma quantidade de " +
                    "apontamentos menor ou igual a zero", codItemOrdemServico));
        }

        if (qtdApontamentosItem > 0 && codItemOrdemServico <= 0) {
            throw new IllegalStateException(String.format("A alternativa de código %d não pode ter um item com %d " +
                    "apontamentos e código menor ou igual a zero", codAlternativa, codItemOrdemServico));
        }
    }

    public boolean jaTemItemPendente() {
        return codItemOrdemServico > 0;
    }

    public long getCodAlternativa() {
        return codAlternativa;
    }

    public long getCodContextoPergunta() {
        return codContextoPergunta;
    }

    public long getCodContextoAlternativa() {
        return codContextoAlternativa;
    }

    public long getCodItemOrdemServico() {
        return codItemOrdemServico;
    }

    @Nullable
    public String getRespostaTipoOutrosAberturaItem() {
        return respostaTipoOutrosAberturaItem;
    }

    public int getQtdApontamentosItem() {
        return qtdApontamentosItem;
    }

    public boolean isDeveAbrirOrdemServico() {
        return deveAbrirOrdemServico;
    }

    public boolean isAlternativaTipoOutros() {
        return alternativaTipoOutros;
    }

    @NotNull
    public PrioridadeAlternativa getPrioridadeAlternativa() {
        return prioridadeAlternativa;
    }
}