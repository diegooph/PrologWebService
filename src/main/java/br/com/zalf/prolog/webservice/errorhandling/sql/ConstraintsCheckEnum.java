package br.com.zalf.prolog.webservice.errorhandling.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-10-15
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public enum ConstraintsCheckEnum {
    DEFAULT(null) {
        @Override
        String getDetailedMessage() {
            return "";
        }
    };

    @Nullable
    private final String constraintCheckName;

    ConstraintsCheckEnum(@Nullable final String constraintCheckName) {
        this.constraintCheckName = constraintCheckName;
    }

    public static ConstraintsCheckEnum fromString(@Nullable final String constraintCheckName) {
        if (constraintCheckName != null) {
            for (final ConstraintsCheckEnum constraintsCheckEnum : ConstraintsCheckEnum.values()) {
                if (constraintsCheckEnum != DEFAULT) {
                    if (constraintsCheckEnum.toString().equalsIgnoreCase(constraintCheckName)) {
                        return constraintsCheckEnum;
                    }
                }
            }
        }
        return DEFAULT;
    }

    @NotNull
    abstract String getDetailedMessage();

    @Override
    public String toString() {
        return asString();
    }

    public String asString() {
        return this.constraintCheckName;
    }
}
