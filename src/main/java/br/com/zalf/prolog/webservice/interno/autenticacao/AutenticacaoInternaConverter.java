package br.com.zalf.prolog.webservice.interno.autenticacao;

import br.com.zalf.prolog.webservice.interno.PrologInternalUser;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created on 2020-10-08
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AutenticacaoInternaConverter {

    private AutenticacaoInternaConverter() {
        throw new IllegalStateException(AutenticacaoInternaConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static Optional<PrologInternalUser> createPrologInternalUser(@NotNull final ResultSet rSet)
            throws SQLException {
        if (rSet.next()) {
            final Long codigo = rSet.getLong("CODIGO");
            return Optional.of(PrologInternalUser
                    .builder()
                    .codigo(codigo)
                    .username(rSet.getString("USERNAME"))
                    .encryptedPassword(rSet.getString("ENCRYPTED_PASSWORD"))
                    .databaseUsername(rSet.getString("DATABASE_USERNAME"))
                    .build());
        } else {
            return Optional.empty();
        }
    }
}
