package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.header;

import com.google.common.base.Charsets;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by luiz on 01/08/17.
 */
public final class HeaderEntry {
    private final String name;
    private final String value;

    private HeaderEntry(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static HeaderEntry create(@NotNull final String name, @NotNull final String value) {
        return new HeaderEntry(name, value);
    }

    public static HeaderEntry createAuthorizationBasic(@NotNull String value) {
        checkNotNull(value);

        return HeaderEntry.create(
                "Authorization",
                "Basic " + Base64.getEncoder().encodeToString(value.getBytes(Charsets.UTF_8)));
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}