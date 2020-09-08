package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.header;

import org.jetbrains.annotations.NotNull;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by luiz on 24/07/17.
 */
public final class HeaderUtils {

    private HeaderUtils() {
        throw new IllegalStateException(HeaderUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    public static void bindHeadersToService(@NotNull BindingProvider bindingProvider, @NotNull HeaderEntry... entries) {
        checkNotNull(bindingProvider);
        checkNotNull(entries);

        final Map<String, Object> map = bindingProvider.getRequestContext();
        final Map<String, List<String>> headers = new HashMap<>();

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < entries.length; i++) {
            headers.put(entries[i].getName(), Collections.singletonList(entries[i].getValue()));
        }

        map.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
    }
}