package br.com.zalf.prolog.webservice.gente.prontuario.model.ocorrencia;

/**
 * Created by Zart on 03/07/2017.
 */
public class Documento extends Ocorrencia{

    private String rs;
    private String ec;
    private String it;

    public Documento() {
    }

    public String getRs() {
        return rs;
    }

    public void setRs(String rs) {
        this.rs = rs;
    }

    public String getEc() {
        return ec;
    }

    public void setEc(String ec) {
        this.ec = ec;
    }

    public String getIt() {
        return it;
    }

    public void setIt(String it) {
        this.it = it;
    }
}
