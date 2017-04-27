package br.com.zalf.prolog.commons.network;

/**
 * Created by luiz on 2/22/16.
 * Padronização das requisições feitas ao WS
 */
@Deprecated
public class Request<T> {
    private T object;
    private final String token;
    private final Long cpf;
    private Long codUnidade;

    public Request(String token, Long cpf, Long codUnidade) {
        this.token = token;
        this.cpf = cpf;
        this.codUnidade = codUnidade;
    }

    public Request(String token, Long cpf) {
        this.token = token;
        this.cpf = cpf;
    }

    public Request(T object, String token, Long cpf, Long codUnidade) {
        this.object = object;
        this.token = token;
        this.cpf = cpf;
        this.codUnidade = codUnidade;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public T getObject() {
        return object;
    }

    public String getToken() {
        return token;
    }

    public Long getCpf() {
        return cpf;
    }

    public Long getCodUnidade() {
        return codUnidade;
    }

    public void setCodUnidade(Long codUnidade) {
        this.codUnidade = codUnidade;
    }
}
