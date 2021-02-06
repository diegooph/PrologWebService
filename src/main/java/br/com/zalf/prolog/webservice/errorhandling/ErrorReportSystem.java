package br.com.zalf.prolog.webservice.errorhandling;

import io.sentry.Sentry;
import io.sentry.SentryEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-10-22
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ErrorReportSystem {

    private ErrorReportSystem() {
        throw new IllegalStateException(ErrorReportSystem.class.getSimpleName() + " cannot be instantiated!");
    }

    public static void logException(@NotNull final Throwable throwable) {
        Sentry.captureException(throwable);
    }

    public static void logEvent(@NotNull final SentryEvent event) {
        Sentry.captureEvent(event);
    }

    public static void logMessage(@NotNull final String message) {
        Sentry.captureMessage(message);
    }
}
