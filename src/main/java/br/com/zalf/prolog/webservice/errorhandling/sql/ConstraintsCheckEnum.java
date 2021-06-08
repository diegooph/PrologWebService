package br.com.zalf.prolog.webservice.errorhandling.sql;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-10-15
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@AllArgsConstructor
public enum ConstraintsCheckEnum {

    DEFAULT("") {
        @Override
        String getDetailMessage(final ValidEntityTableName tableName) {
            return "Constraint não informada";
        }
    },
    CHECK_STATUS_ATIVO_ACOPLAMENTO("check_status_ativo_acoplamento") {
        @Override
        String getDetailMessage(final ValidEntityTableName tableName) {
            return String.format("%s contém acoplamento.", tableName.getTableName());
        }
    };

    private final String constraintCheckName;

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

    @Override
    public String toString() {
        return asString();
    }

    public String asString() {
        return this.constraintCheckName;
    }

    abstract String getDetailMessage(ValidEntityTableName tableName);
}
