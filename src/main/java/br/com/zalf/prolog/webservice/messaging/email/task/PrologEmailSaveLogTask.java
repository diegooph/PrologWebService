package br.com.zalf.prolog.webservice.messaging.email.task;

import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.messaging.MessageScope;
import br.com.zalf.prolog.webservice.messaging.email._model.EmailRequestResponseHolder;
import com.google.common.base.Throwables;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;

import static br.com.zalf.prolog.webservice.commons.util.StatementUtils.bindValueOrNull;

/**
 * Created on 2020-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PrologEmailSaveLogTask {

    public void saveToDatabase(@NotNull final Connection connection,
                               @NotNull final MessageScope messageScope,
                               @Nullable final EmailRequestResponseHolder holder,
                               @Nullable final Throwable fatalSendException) throws Throwable {
        final String request = holder != null ? holder.getRequestAsJsonOrNull() : null;
        final String response = holder != null ? holder.getResponseAsJsonOrNull() : null;
        final String fatalSendExceptionString = getFatalSendExceptionAsStringOrNull(fatalSendException);

        try (final PreparedStatement stmt = connection.prepareCall("{CALL MESSAGING.FUNC_EMAIL_SALVA_LOG(" +
                "F_DATA_HORA_ATUAL      => ?," +
                "F_EMAIL_MESSAGE_SCOPE  => ?," +
                "F_REQUEST_TO_API       => ?," +
                "F_RESPONSE_FROM_API    => ?," +
                "F_FATAL_SEND_EXCEPTION => ?)}")) {
            stmt.setObject(1, Now.getOffsetDateTimeUtc());
            stmt.setString(2, messageScope.asString());
            if (request != null) {
                stmt.setObject(3, PostgresUtils.toJsonb(request));
            } else {
                stmt.setNull(3, Types.NULL);
            }
            if (response != null) {
                stmt.setObject(4, PostgresUtils.toJsonb(response));
            } else {
                stmt.setNull(4, Types.NULL);
            }
            bindValueOrNull(stmt, 5, fatalSendExceptionString, SqlType.TEXT);
            stmt.execute();
        }
    }

    @Nullable
    private String getFatalSendExceptionAsStringOrNull(@Nullable final Throwable fatalSendException) {
        return fatalSendException != null ? Throwables.getStackTraceAsString(fatalSendException) : null;
    }
}
