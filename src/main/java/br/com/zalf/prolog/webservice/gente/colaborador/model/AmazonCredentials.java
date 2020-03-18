package br.com.zalf.prolog.webservice.gente.colaborador.model;

import org.jetbrains.annotations.NotNull;

/**
 * Classe responsável por conter as credenciais de acesso a Amazon.
 */
public class AmazonCredentials {
    @NotNull
    private String user;

    @NotNull
    private String accessKeyId;

    @NotNull
    private String secretAccessKey;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(@NotNull String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public void setSecretAccessKey(@NotNull String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
    }
}