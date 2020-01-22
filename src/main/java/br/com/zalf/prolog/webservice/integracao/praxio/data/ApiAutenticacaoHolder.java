package br.com.zalf.prolog.webservice.integracao.praxio.data;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 11/27/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiAutenticacaoHolder {
    @NotNull
    private final String url;
    @NotNull
    private final String apiTokenClient;
    @NotNull
    private final Long apiShortCode;

    public ApiAutenticacaoHolder(@NotNull final String url,
                                 @NotNull final String apiTokenClient,
                                 @NotNull final Long apiShortCode) {
        this.url = url;
        this.apiTokenClient = apiTokenClient;
        this.apiShortCode = apiShortCode;
    }

    @NotNull
    public String getUrl() {
        return url;
    }

    @NotNull
    public String getApiTokenClient() {
        return apiTokenClient;
    }

    @NotNull
    public Long getApiShortCode() {
        return apiShortCode;
    }
}
