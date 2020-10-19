package br.com.zalf.prolog.webservice.errorhandling.error;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-10-16
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public enum StatusEnum {
    UNPROCESSABLE_ENTITY(422);

    @NotNull
    private final Integer statusCode;

    StatusEnum(@NotNull final Integer statusCode) {
        this.statusCode = statusCode;
    }

    @NotNull
    public static StatusEnum fromInteger(@Nullable final Integer integer) throws IllegalArgumentException {
        if (integer != null) {
            for (final StatusEnum statusEnum : StatusEnum.values()) {
                if (integer.equals(statusEnum.getAsInteger())) {
                    return statusEnum;
                }
            }
        }
        throw new IllegalArgumentException("Nenhuma codigo http encontrado para o Integer: " + integer);
    }

    @NotNull
    public Integer getAsInteger() {
        return this.statusCode;
    }
}
