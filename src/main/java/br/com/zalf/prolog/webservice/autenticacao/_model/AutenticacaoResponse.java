package br.com.zalf.prolog.webservice.autenticacao._model;

/**
 * Created by luiz on 1/16/16.
 * Objeto gerado após a autenticação no ws
 */
public final class AutenticacaoResponse {
    /**
     * variáveis públicas utilizadas para setar o atributo status.
     */
    public static final String OK = "OK";
    public static final String ERROR = "ERROR";
    private Long cpf;
    private String token;
    private String status;


    public AutenticacaoResponse() {
    }

    public AutenticacaoResponse(final String status, final Long cpf, final String token) {
        this.status = status;
        this.cpf = cpf;
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public Long getCpf() {
        return cpf;
    }

    public void setCpf(final Long cpf) {
        this.cpf = cpf;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }
}
