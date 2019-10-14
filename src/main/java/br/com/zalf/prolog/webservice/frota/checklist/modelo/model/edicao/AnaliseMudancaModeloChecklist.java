package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Created on 2019-09-12
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AnaliseMudancaModeloChecklist {
    private final boolean algoMudouNoModelo;
    private final boolean algoMudouNoContexto;
    private final boolean deveCriarNovaVersaoModelo;
    @Nullable
    private final Map<Long, AnaliseItemModeloChecklist> analisePerguntas;
    @Nullable
    private final Map<Long, AnaliseItemModeloChecklist> analiseAlternativas;

    public AnaliseMudancaModeloChecklist(
            final boolean algoMudouNoModelo,
            final boolean algoMudouNoContexto,
            final boolean deveCriarNovaVersaoModelo,
            @Nullable final Map<Long, AnaliseItemModeloChecklist> analisePerguntas,
            @Nullable final Map<Long, AnaliseItemModeloChecklist> analiseAlternativas) {
        this.algoMudouNoModelo = algoMudouNoModelo;
        this.algoMudouNoContexto = algoMudouNoContexto;
        this.deveCriarNovaVersaoModelo = deveCriarNovaVersaoModelo;
        this.analisePerguntas = analisePerguntas;
        this.analiseAlternativas = analiseAlternativas;
    }

    public boolean isAlgoMudouNoModelo() {
        return algoMudouNoModelo;
    }

    public boolean isAlgoMudouNoContexto() { return algoMudouNoContexto; }

    public boolean isDeveCriarNovaVersaoModelo() {
        return deveCriarNovaVersaoModelo;
    }

    @NotNull
    public AnaliseItemModeloChecklist getPergunta(@NotNull final Long codPergunta) {
        if (analisePerguntas == null) {
            throw new IllegalStateException("analisePerguntas == null!");
        }

        return analisePerguntas.get(codPergunta);
    }

    @NotNull
    public AnaliseItemModeloChecklist getAlternativa(@NotNull final Long codAlternativa) {
        if (analiseAlternativas == null) {
            throw new IllegalStateException("analiseAlternativas == null!");
        }

        return analiseAlternativas.get(codAlternativa);
    }
}