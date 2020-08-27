package br.com.zalf.prolog.webservice.integracao.agendador.os._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-08-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public class InfosEnvioOsIntegracao {
    @NotNull
    private final String urlEnvio;
    @Nullable
    private final String apiTokenClient;
    @Nullable
    private final String apiShortCode;
}
