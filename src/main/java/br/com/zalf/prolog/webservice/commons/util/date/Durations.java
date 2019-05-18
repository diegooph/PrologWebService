package br.com.zalf.prolog.webservice.commons.util.date;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Created on 24/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class Durations {

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

    @NotNull
    public static String formatDuration(final long millis, @NotNull final Format format) {
        return DurationFormatUtils.formatDuration(millis, format.getFormat());
    }

    @NotNull
    public static String formatDurationHandleNegative(final long millis, @NotNull final Format format) {
        if (millis < 0) {
            return "-" + formatDuration(Math.abs(millis), format);
        } else {
            return formatDuration(millis, format);
        }
    }

    @NotNull
    public static String formatDuration(@NotNull final Duration duration, @NotNull final Format format) {
        return formatDuration(duration.toMillis(), format);
    }
}