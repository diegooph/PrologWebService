package br.com.zalf.prolog.webservice.integracao.api.pneu.cadastro.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 16/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiPneuCargaInicialResponse {
    @NotNull
    public static final String SUCCESS_MESSAGE = "Pneu cadastrado com sucesso no Sistema ProLog";
    @NotNull
    public static final String ERROR_MESSAGE = "Não foi possível cadastrar o pneu no Sistema ProLog";
    @NotNull
    private final Long codigoSistemaIntegrado;
    @NotNull
    private final String codigoCliente;
    @NotNull
    private final Boolean sucesso;
    @NotNull
    private final String mensagem;
    @Nullable
    private final Long codPneuProLog;
    @Nullable
    private final Throwable throwable;

    @NotNull
    public static ApiPneuCargaInicialResponse ok(@NotNull final Long codigoSistemaIntegrado,
                                                 @NotNull final String codigoCliente,
                                                 @NotNull final Long codPneuProLog) {
        return new ApiPneuCargaInicialResponse(
                codigoSistemaIntegrado,
                codigoCliente,
                true,
                SUCCESS_MESSAGE,
                codPneuProLog,
                null);
    }

    @NotNull
    public static ApiPneuCargaInicialResponse error(@NotNull final Long codigoSistemaIntegrado,
                                                    @NotNull final String codigoCliente,
                                                    @NotNull final Throwable throwable) {
        return new ApiPneuCargaInicialResponse(
                codigoSistemaIntegrado,
                codigoCliente,
                false,
                ERROR_MESSAGE,
                null,
                throwable);
    }

    @NotNull
    public static ApiPneuCargaInicialResponse error(@NotNull final Long codigoSistemaIntegrado,
                                                    @NotNull final String codigoCliente,
                                                    @NotNull final String errorMessage) {
        return new ApiPneuCargaInicialResponse(
                codigoSistemaIntegrado,
                codigoCliente,
                false,
                errorMessage,
                null,
                null);
    }

    private ApiPneuCargaInicialResponse(@NotNull final Long codigoSistemaIntegrado,
                                        @NotNull final String codigoCliente,
                                        @NotNull final Boolean sucesso,
                                        @NotNull final String mensagem,
                                        @Nullable final Long codPneuProLog,
                                        @Nullable final Throwable throwable) {
        this.codigoSistemaIntegrado = codigoSistemaIntegrado;
        this.codigoCliente = codigoCliente;
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.codPneuProLog = codPneuProLog;
        this.throwable = throwable;
    }

    @NotNull
    public Long getCodigoSistemaIntegrado() {
        return codigoSistemaIntegrado;
    }

    @NotNull
    public String getCodigoCliente() {
        return codigoCliente;
    }

    @NotNull
    public Boolean getSucesso() {
        return sucesso;
    }

    @NotNull
    public String getMensagem() {
        return mensagem;
    }

    @Nullable
    public Long getCodPneuProLog() {
        return codPneuProLog;
    }

    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }
}
