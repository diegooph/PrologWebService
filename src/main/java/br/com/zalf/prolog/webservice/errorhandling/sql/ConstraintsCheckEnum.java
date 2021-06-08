package br.com.zalf.prolog.webservice.errorhandling.sql;

import com.google.api.client.util.Preconditions;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

/**
 * Created on 2020-10-15
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@AllArgsConstructor
public enum ConstraintsCheckEnum {

    DEFAULT("") {
        @Override
        @NotNull
        String getDetailMessage(@Nullable final ValidEntityTableName tableName) {
            return "Constraint não informada";
        }
    },
    CHECK_STATUS_ATIVO_ACOPLAMENTO("check_status_ativo_acoplamento") {
        @Override
        @NotNull
        String getDetailMessage(@Nullable final ValidEntityTableName tableName) {
            Preconditions.checkNotNull(tableName);
            return String.format("%s contém acoplamento.", tableName.getTableName());
        }
    };

    private final String constraintCheckName;

    public static ConstraintsCheckEnum fromString(@NotNull final String constraintCheckName) {
        return Stream.of(ConstraintsCheckEnum.values())
                .filter(e -> e.constraintCheckName.equalsIgnoreCase(constraintCheckName))
                .findFirst()
                .orElse(DEFAULT);
    }

    @Override
    public String toString() {
        return asString();
    }

    public String asString() {
        return this.constraintCheckName;
    }

    @NotNull
    abstract String getDetailMessage(@Nullable ValidEntityTableName tableName);
}
