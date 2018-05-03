package br.com.zalf.prolog.webservice.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;

/**
 * Created on 03/05/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class ResponseImagemChecklist extends ResponseWithCod {
    private String urlImagem;

    private ResponseImagemChecklist() {

    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(final String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public static ResponseImagemChecklist ok(String msg, Long codImagem, String urlImagem){
        final ResponseImagemChecklist r = new ResponseImagemChecklist();
        r.setStatus(OK);
        r.setMsg(msg);
        r.setCodigo(codImagem);
        r.setUrlImagem(urlImagem);
        return r;
    }
}