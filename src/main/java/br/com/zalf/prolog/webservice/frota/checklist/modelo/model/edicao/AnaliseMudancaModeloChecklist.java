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
    private final boolean deveCriarNovaVersaoModelo;
    @Nullable
    private final Map<Long, AnaliseItemModeloChecklist> analisePerguntas;
    @Nullable
    private final Map<Long, AnaliseItemModeloChecklist> analiseAlternativas;

    public AnaliseMudancaModeloChecklist(
            final boolean algoMudouNoModelo,
            final boolean deveCriarNovaVersaoModelo,
            @Nullable final Map<Long, AnaliseItemModeloChecklist> analisePerguntas,
            @Nullable final Map<Long, AnaliseItemModeloChecklist> analiseAlternativas) {
        this.algoMudouNoModelo = algoMudouNoModelo;
        this.deveCriarNovaVersaoModelo = deveCriarNovaVersaoModelo;
        this.analisePerguntas = analisePerguntas;
        this.analiseAlternativas = analiseAlternativas;
    }

    public boolean isAlgoMudouNoModelo() {
        return algoMudouNoModelo;
    }

    public boolean isDeveCriarNovaVersaoModelo() {
        return deveCriarNovaVersaoModelo;
    }

    @NotNull
    public AnaliseItemModeloChecklist getPergunta(@NotNull final Long codPergunta) {
        return analisePerguntas.get(codPergunta);
    }

    @NotNull
    public AnaliseItemModeloChecklist getAlternativa(@NotNull final Long codAlternativa) {
        return analiseAlternativas.get(codAlternativa);
    }
}