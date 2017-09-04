package br.com.zalf.prolog.webservice.interceptors.auth;

public enum AuthType {
    BASIC("Basic"),
    BEARER("Bearer");

    private final String value;

    AuthType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}