package br.com.zalf.prolog.webservice.dashboard.base;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Essa classe é utilizada para podermos identificar os tipos de componentes de um modo mais verboso e de fácil
 * identificação do que seu código. A string {@link IdentificadorTipoComponente#identificador} é usada inclusive
 * para fazer funcionar a serialização/desserialização em JSON utilizando Gson.
 *
 * Created on 2/6/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum IdentificadorTipoComponente {
    GRAFICO_BARRAS_VERTICAIS("GRAFICO_BARRAS_VERTICAIS"),
    GRAFICO_BARRAS_VERTICAIS_AGRUPADAS("GRAFICO_BARRAS_VERTICAIS_AGRUPADAS"),
    GRAFICO_DENSIDADE("GRAFICO_DENSIDADE"),
    GRAFICO_LINHAS_HORIZONTAIS("GRAFICO_LINHAS_HORIZONTAIS"),
    GRAFICO_LINHAS_VERTICAIS("GRAFICO_LINHAS_VERTICAIS"),
    QUANTIDADE_ITEM("QUANTIDADE_ITEM"),
    GRAFICO_SETORES("GRAFICO_SETORES"),
    TABELA("TABELA");

    @NotNull
    private final String identificador;

    IdentificadorTipoComponente(@NotNull final String identificador) {
        this.identificador = identificador;
    }

    @Override
    public String toString() {
        return asString();
    }

    @NotNull
    public String asString() {
        return identificador;
    }

    @NotNull
    public static IdentificadorTipoComponente fromString(@Nonnull final String string) {
        Preconditions.checkNotNull(string, "string cannot be null!");

        for (final IdentificadorTipoComponente tipo : IdentificadorTipoComponente.values()) {
            if (string.equals(tipo.identificador)) {
                return tipo;
            }
        }

        throw new IllegalArgumentException("Nenhum tipo de componente encontrado para a string: " + string);
    }
}