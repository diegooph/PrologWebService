package br.com.zalf.prolog.webservice.commons.network;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by luiz on 7/12/16.
 */
@ApiModel(description = "Resposta default da API.")
public abstract class AbstractResponse {
    public static final String OK = "OK";
    public static final String ERROR = "ERROR";
    @ApiModelProperty(
            value = "Constante indicando se a operação solicitada foi sucesso ou erro.",
            allowableValues = "OK, ERROR",
            required = true)
    private String status;
    @ApiModelProperty(
            value = "Mensagem descrevendo o resultado de sucesso ou erro da operação.")
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    @ApiModelProperty(hidden = true)
    public boolean isOk() {
        return status.equals(OK);
    }
}
