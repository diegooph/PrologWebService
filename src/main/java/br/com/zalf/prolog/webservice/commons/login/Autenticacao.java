package br.com.zalf.prolog.webservice.commons.login;

/**
 * Created by luiz on 1/16/16.
 * Objeto gerado após a autenticação no ws
 */
public class Autenticacao {
    private Long cpf;
    private String token;
    private String status;
    /**
     * variáveis públicas utilizadas para setar o atributo status
     */
    public static final String OK = "OK";
    public static final String ERROR = "ERROR";


    public Autenticacao() {
    }

    public Autenticacao(String status, Long cpf, String token) {
        this.status = status;
        this.cpf = cpf;
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCpf() {
        return cpf;
    }

    public void setCpf(Long cpf) {
        this.cpf = cpf;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
