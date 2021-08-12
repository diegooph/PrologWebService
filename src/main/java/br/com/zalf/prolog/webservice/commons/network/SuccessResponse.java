package br.com.zalf.prolog.webservice.commons.network;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-11-09
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class SuccessResponse {
    @Nullable
    private final Long uniqueItemId;
    @NotNull
    private final String message;
}
