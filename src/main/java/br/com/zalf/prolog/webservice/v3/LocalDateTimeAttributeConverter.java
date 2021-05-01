package br.com.zalf.prolog.webservice.v3;

import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Created on 2021-04-22
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Component("localDateTimeConverter")
public final class LocalDateTimeAttributeConverter {
    @Nullable
    public LocalDateTime fromInstantUtc(@Nullable final Instant instant) {
        return instant == null ? null : instant.atZone(ZoneOffset.UTC).toLocalDateTime();
    }
}