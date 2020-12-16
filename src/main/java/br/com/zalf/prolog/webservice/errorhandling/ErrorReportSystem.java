package br.com.zalf.prolog.webservice.errorhandling;

import br.com.zalf.prolog.webservice.commons.util.EnvironmentHelper;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogUtils;
import br.com.zalf.prolog.webservice.config.BuildConfig;
import io.sentry.Sentry;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-10-22
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ErrorReportSystem {
    private static final String TAG = ErrorReportSystem.class.getSimpleName();
    private static boolean initialized = false;

    private ErrorReportSystem() {
        throw new IllegalStateException(ErrorReportSystem.class.getSimpleName() + " cannot be instantiated!");
    }

    public static void init() {
        if (!ProLogUtils.isDebug()) {
            Sentry.init(sentryOptions -> {
                sentryOptions.setDsn(EnvironmentHelper.SENTRY_DSN + "?release=" + BuildConfig.VERSION_CODE);
                sentryOptions.setEnableSessionTracking(true);
                sentryOptions.setAttachStacktrace(true);
                sentryOptions.addInAppExclude("sun");
                sentryOptions.addInAppExclude("java");
                sentryOptions.addInAppExclude("org");
                sentryOptions.addInAppInclude("br.com.zalf");
            });
            initialized = true;
        }
    }

    public static void logException(@NotNull final Throwable throwable) {
        if (initialized) {
            Sentry.captureException(throwable);
        } else {
            Log.w(TAG, "Tried to log an exception on a not initialized error report system. " +
                    "Call init() first!");
        }
    }
    
    public static void logMessage(@NotNull final String message) {
        if (initialized) {
            Sentry.captureMessage(message);
        } else {
            Log.w(TAG, "Tried to log a message on a not initialized error report system. " +
                    "Call init() first!");
        }
    }
}
