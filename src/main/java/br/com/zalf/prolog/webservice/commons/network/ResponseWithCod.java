package br.com.zalf.prolog.webservice.commons.network;

/**
 * Created by jean on 10/07/16.
 */
public class ResponseWithCod extends AbstractResponse {
    private Long codigo;

    public ResponseWithCod(){
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public static ResponseWithCod Ok(String msg, Long cod){
        ResponseWithCod r = new ResponseWithCod();
        r.setStatus(OK);
        r.setMsg(msg);
        r.setCodigo(cod);
        return r;
    }
}
