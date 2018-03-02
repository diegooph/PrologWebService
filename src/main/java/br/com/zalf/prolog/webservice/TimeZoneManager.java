package br.com.zalf.prolog.webservice;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created on 3/2/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class TimeZoneManager {

    private TimeZoneManager() {
        throw new IllegalStateException(TimeZoneManager.class.getSimpleName() + " cannot be instantiated!");
    }

    public static LocalDateTime getZonedLocalDateTimeForCpf(@NotNull final Long cpf, @NotNull final Connection connection) {
        return LocalDateTime
                .now(ZoneId.of("America/Sao_Paulo"));
    }

    public static LocalDateTime getZonedLocalDateTimeForToken(@NotNull final String token, @NotNull final Connection connection) {
        return LocalDateTime
                .now(ZoneId.of("America/Sao_Paulo"));
    }

    public static LocalDateTime getZonedLocalDateTimeForCodUnidade(@NotNull final Long codUnidade, @NotNull final Connection connection) {
        return LocalDateTime
                .now(ZoneId.of("America/Sao_Paulo"));
    }
}