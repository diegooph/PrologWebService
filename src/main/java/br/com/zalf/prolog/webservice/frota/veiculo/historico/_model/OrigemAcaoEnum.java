package br.com.zalf.prolog.webservice.frota.veiculo.historico._model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-09-14
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public enum OrigemAcaoEnum {
    /**
     * Ação com origem na API externa do Prolog.
     */
    API("API"),

    /**
     * Ação com origem no próprio sistema do Prolog via web.
     */
    PROLOG_WEB("PROLOG_WEB"),

    /**
     * Ação com origem no próprio sistema do Prolog via app android.
     */
    PROLOG_ANDROID("PROLOG_ANDROID"),

    /**
     * Ação com origem no sistema interno do Prolog. Usado para auxiliar em demandas de suporte e implantação.
     */
    INTERNO("INTERNO"),

    /**
     * Ação com origem em um suporte aberto pelo cliente.
     */
    SUPORTE("SUPORTE");

    @NotNull
    private final String stringRepresentation;

    OrigemAcaoEnum(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public static OrigemAcaoEnum fromString(@Nullable final String text) throws IllegalArgumentException {
        if (text != null) {
            for (final OrigemAcaoEnum origemAcaoEnum : OrigemAcaoEnum.values()) {
                if (text.equalsIgnoreCase(origemAcaoEnum.asString())) {
                    return origemAcaoEnum;
                }
            }
        }
        throw new IllegalArgumentException("Nenhuma origem encontrada para a String: " + text);
    }

    @Override
    public String toString() {
        return asString();
    }

    @NotNull
    public String asString() {
        return stringRepresentation;
    }
}
