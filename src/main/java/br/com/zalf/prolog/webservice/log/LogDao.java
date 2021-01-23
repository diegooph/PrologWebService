package br.com.zalf.prolog.webservice.log;

import br.com.zalf.prolog.webservice.log._model.RequestLog;
import br.com.zalf.prolog.webservice.log._model.ResponseLog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

/**
 * Created by didi on 9/15/16.
 */
public interface LogDao {

    boolean insert(@NotNull final String log, @NotNull final String identificador) throws SQLException;

    void insertRequestResponseLog(@NotNull final RequestLog requestLog,
                                  @Nullable final ResponseLog responseLog) throws Throwable;
}
