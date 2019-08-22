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
    private final String novoCodigoCliente;
    @NotNull
    private final Boolean sucesso;
    @NotNull
    private final String mensagem;
    @Nullable
    private final Long codPneuProLog;

    public ApiPneuCargaInicialResponse(@NotNull final Long codigoSistemaIntegrado,
                                       @NotNull final String novoCodigoCliente,
                                       @NotNull final Boolean sucesso,
                                       @NotNull final String mensagem,
                                       @Nullable final Long codPneuProLog) {
        this.codigoSistemaIntegrado = codigoSistemaIntegrado;
        this.novoCodigoCliente = novoCodigoCliente;
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.codPneuProLog = codPneuProLog;
    }

    @NotNull
    public Long getCodigoSistemaIntegrado() {
        return codigoSistemaIntegrado;
    }

    @NotNull
    public String getNovoCodigoCliente() {
        return novoCodigoCliente;
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
}
