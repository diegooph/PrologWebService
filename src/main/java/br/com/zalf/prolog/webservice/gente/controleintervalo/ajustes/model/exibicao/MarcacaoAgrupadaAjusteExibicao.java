package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.model.exibicao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Agrupa uma marcação de início com sua equivalente de fim para exibição.
 *
 * Poderá conter apenas início ou apenas fim.
 *
 * Created on 09/03/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class MarcacaoAgrupadaAjusteExibicao {
    /**
     * Uma marcação de início para exibição, pode ser <code>null</code>.
     */
    @Nullable
    private final MarcacaoAjusteExibicao marcacaoInicio;

    /**
     * Uma marcação de fim para exibição, pode ser <code>null</code>.
     */
    @Nullable
    private final MarcacaoAjusteExibicao marcacaoFim;

    public MarcacaoAgrupadaAjusteExibicao(@Nullable final MarcacaoAjusteExibicao marcacaoInicio,
                                          @Nullable final MarcacaoAjusteExibicao marcacaoFim) {
        this.marcacaoInicio = marcacaoInicio;
        this.marcacaoFim = marcacaoFim;
    }

    @NotNull
    public static MarcacaoAgrupadaAjusteExibicao createDummy() {
        return new MarcacaoAgrupadaAjusteExibicao(
                MarcacaoAjusteExibicao.createDummyInicio(),
                MarcacaoAjusteExibicao.createDummyFim());
    }

    @Nullable
    public MarcacaoAjusteExibicao getMarcacaoInicio() {
        return marcacaoInicio;
    }

    @Nullable
    public MarcacaoAjusteExibicao getMarcacaoFim() {
        return marcacaoFim;
    }

    @Override
    public String toString() {
        return "MarcacaoAgrupadaAjusteExibicao{" +
                "marcacaoInicio=" + marcacaoInicio +
                ", marcacaoFim=" + marcacaoFim +
                '}';
    }
}