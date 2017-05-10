package br.com.zalf.prolog.webservice.commons.network;

/**
 * Created by luiz on 7/12/16.
 */
public abstract class AbstractResponse {
    private String status;
    private String msg;
    public static final String OK = "OK";
    public static final String ERROR = "ERROR";

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
