package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2019-08-17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ModeloChecklistRealizacao {
    /**
     * Número que representa o código que o modelo de checklist possui. Identificador único do modelo de checklist.
     */
    @NotNull
    private final Long codModelo;

    /**
     * O código da versão atual desse modelo.
     */
    @NotNull
    private final Long codVersaoModelo;

    @NotNull
    private final VeiculoChecklistRealizacao veiculoRealizacao;


    @NotNull
    private final List<PerguntaRealizacaoChecklist> perguntas;

    public ModeloChecklistRealizacao(@NotNull final Long codModelo,
                                     @NotNull final Long codVersaoModelo,
                                     @NotNull final VeiculoChecklistRealizacao veiculoRealizacao,
                                     @NotNull final List<PerguntaRealizacaoChecklist> perguntas) {
        this.codModelo = codModelo;
        this.codVersaoModelo = codVersaoModelo;
        this.veiculoRealizacao = veiculoRealizacao;
        this.perguntas = perguntas;
    }

    @NotNull
    public Long getCodModelo() {
        return codModelo;
    }

    @NotNull
    public Long getCodVersaoModelo() {
        return codVersaoModelo;
    }

    @NotNull
    public VeiculoChecklistRealizacao getVeiculoRealizacao() {
        return veiculoRealizacao;
    }

    @NotNull
    public List<PerguntaRealizacaoChecklist> getPerguntas() {
        return perguntas;
    }
}
