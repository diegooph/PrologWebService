package br.com.zalf.prolog.webservice.v3;

import org.jetbrains.annotations.Nullable;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Created on 2021-04-26
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Converter
public class LocalDateTimeUtcAttributeConverter implements AttributeConverter<LocalDateTime, Timestamp> {
    @Override
    public Timestamp convertToDatabaseColumn(@Nullable final LocalDateTime localDateTime) {
        return localDateTime == null ? null : Timestamp.valueOf(localDateTime);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(@Nullable final Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime();
    }
}