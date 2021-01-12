package br.com.zalf.prolog.webservice.commons.util.datetime;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Created on 24/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class DurationUtils {
    public enum Format {
        HH_MM_SS("HH:mm:ss");

        @NotNull
        private final String format;

        Format(@NotNull final String format) {
            this.format = format;
        }

        @NotNull
        public String getFormat() {
            return format;
        }
    }

    private DurationUtils() {
        throw new UnsupportedOperationException("An util class cannot be instatied!");
    }

    @NotNull
    public static String formatDuration(final long durationInMillisToFormat, @NotNull final Format format) {
        return DurationFormatUtils.formatDuration(durationInMillisToFormat, format.getFormat());
    }

    @NotNull
    public static String formatDuration(@NotNull final Duration durationToFormat, @NotNull final Format format) {
        return formatDuration(durationToFormat.toMillis(), format);
    }

    @NotNull
    public static String formatDurationHandleNegative(final long durationsInMillisToFormat,
                                                      @NotNull final Format format) {
        if (isDurationNegative(durationsInMillisToFormat)) {
            return "-" + formatDuration(Math.abs(durationsInMillisToFormat), format);
        }
        return formatDuration(durationsInMillisToFormat, format);
    }

    private static boolean isDurationNegative(final long durationsInMillisToFormat) {
        return durationsInMillisToFormat < 0;
    }
}