package br.com.zalf.prolog.webservice.messaging.email.task;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnectionActions;
import br.com.zalf.prolog.webservice.messaging.MessageScope;
import br.com.zalf.prolog.webservice.messaging.email._model.EmailRequestResponseHolder;
import com.google.common.base.Throwables;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Optional;

import static br.com.zalf.prolog.webservice.commons.util.database.StatementUtils.bindValueOrNull;

/**
 * Created on 2020-02-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Repository
public class PrologEmailSaveLogTask {

    private static final String TAG = PrologEmailSaveLogTask.class.getSimpleName();
    private final DatabaseConnectionActions actions;

    @Autowired
    PrologEmailSaveLogTask(final DatabaseConnectionActions actions) {
        this.actions = actions;
    }

    public void saveToDatabase(@NotNull final MessageScope messageScope,
                               @Nullable final EmailRequestResponseHolder holder,
                               @Nullable final Throwable fatalSendException) {
        final Optional<String> request = holder.getRequestAsJson();
        final Optional<String> response = holder.getResponseAsJson();
        final String fatalSendExceptionString = getFatalSendExceptionAsStringOrNull(fatalSendException);

        try (final PreparedStatement stmt = connection.prepareCall("{CALL MESSAGING.FUNC_EMAIL_SALVA_LOG(" +
                "F_DATA_HORA_ATUAL      => ?," +
                "F_EMAIL_MESSAGE_SCOPE  => ?," +
                "F_REQUEST_TO_API       => ?," +
                "F_RESPONSE_FROM_API    => ?," +
                "F_FATAL_SEND_EXCEPTION => ?)}")) {
            stmt.setObject(1, Now.getOffsetDateTimeUtc());
            stmt.setString(2, messageScope.asString());

            if (request.isPresent()) {
                stmt.setObject(3, PostgresUtils.toJsonb(request.get()));
            } else {
                stmt.setNull(3, Types.NULL);
            }
            if (response.isPresent()) {
                stmt.setObject(4, PostgresUtils.toJsonb(response.get()));
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
